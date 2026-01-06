#!/bin/bash
# =================================================================
# Nginx 1.28.0 高性能自动化安装脚本
# 支持系统: Ubuntu / CentOS 7+
# 集成模块: Jemalloc, Brotli, Cache_Purge, Http_Concat
# =================================================================
set -e

NGINX_VERSION="1.28.1"
JEMALLOC_VERSION="5.3.0"
HTTP_CONCAT_VERSION="1.2.2"
CACHE_PURGE_VERSION="2.3"

INSTALL_DIR="/usr/local/nginx"
NGINX_USER="nginx"
NGINX_GROUP="nginx"
CURRENT_DIR=$(pwd)
SOURCE_DIR="${CURRENT_DIR}/sources"

log() { echo -e "\033[32m$(date '+%Y-%m-%d %H:%M:%S') [INFO] ${1}\033[0m"; }
log_warn() { echo -e "\033[33m$(date '+%Y-%m-%d %H:%M:%S') [WARN] ${1}\033[0m"; }
log_error() { echo -e "\033[31m$(date '+%Y-%m-%d %H:%M:%S') [ERROR] ${1}\033[0m"; exit 1; }

prepare_env() {
  log "开始检查系统依赖..."
  if [ -f /etc/os-release ]; then
    OS_NAME=$(grep "^NAME=" /etc/os-release | awk -F = '{print $2}' | sed 's/\"//g')
  else
    log_error "无法识别操作系统类型"
  fi

  if [[ "$OS_NAME" == *"Ubuntu"* ]]; then
    apt update -qq
    apt install -y -q gcc g++ make cmake autoconf libpcre3-dev zlib1g-dev libssl-dev wget git bzip2 >/dev/null
  elif [[ "$OS_NAME" == *"CentOS"* ]]; then
    yum install -y -q epel-release
    yum install -y -q gcc gcc-c++ make cmake autoconf pcre-devel zlib-devel openssl-devel wget git bzip2 >/dev/null
  else
    log_error "当前脚本仅支持 Ubuntu 或 CentOS 系统"
  fi
}

prepare_sources() {
  cd "${SOURCE_DIR}"
  log "检查并准备源码包..."

  declare -A urls=(
    ["nginx-${NGINX_VERSION}.tar.gz"]="http://nginx.org/download/nginx-${NGINX_VERSION}.tar.gz"
    ["jemalloc-${JEMALLOC_VERSION}.tar.bz2"]="https://github.com/jemalloc/jemalloc/releases/download/${JEMALLOC_VERSION}/jemalloc-${JEMALLOC_VERSION}.tar.bz2"
    ["nginx-http-concat-${HTTP_CONCAT_VERSION}.tar.gz"]="https://github.com/alibaba/nginx-http-concat/archive/refs/tags/${HTTP_CONCAT_VERSION}.tar.gz"
    ["ngx_cache_purge-${CACHE_PURGE_VERSION}.tar.gz"]="https://github.com/FRiCKLE/ngx_cache_purge/archive/refs/tags/${CACHE_PURGE_VERSION}.tar.gz"
  )

  for file in "${!urls[@]}"; do
    if [ -f "${file}" ]; then
      log "已存在: ${file}"
    else
      log "正在下载: ${file} ..."
      wget -q "${urls[$file]}" -O "${file}" || log_error "下载 ${file} 失败"
    fi
  done

  # Brotli 模块处理
  if [ -d "ngx_brotli" ]; then
    log "检测到 ngx_brotli，更新子模块..."
    cd ngx_brotli && git submodule update --init --recursive && cd ..
  else
    log "克隆 ngx_brotli..."
    git clone --recurse-submodules -j8 https://github.com/google/ngx_brotli
  fi
}

install_jemalloc() {
  cd "${SOURCE_DIR}"
  if ldconfig -p | grep -q jemalloc; then
    log "Jemalloc 已安装"
  else
    log "编译 Jemalloc..."
    tar -jxf "jemalloc-${JEMALLOC_VER}.tar.bz2"
    pushd "jemalloc-${JEMALLOC_VER}" >/dev/null
    ./autogen.sh
    # shellcheck disable=SC2046
    make -j$(nproc) && make install
    echo '/usr/local/lib' > /etc/ld.so.conf.d/local.conf && ldconfig
    popd >/dev/null
    rm -rf "jemalloc-${JEMALLOC_VER}"
  fi
}

install_nginx() {
  cd "${SOURCE_DIR}"
  log "开始 Nginx 编译流程..."

  # 准备第三方模块
  rm -rf nginx-http-concat ngx_cache_purge
  tar xzf "nginx-http-concat-${HTTP_CONCAT_VERSION}.tar.gz" && mv "nginx-http-concat-${HTTP_CONCAT_VERSION}" nginx-http-concat
  tar xzf "ngx_cache_purge-${CACHE_PURGE_VERSION}.tar.gz" && mv "ngx_cache_purge-${CACHE_PURGE_VERSION}" ngx_cache_purge

  # 预编译 Brotli
  pushd ngx_brotli/deps/brotli >/dev/null
  mkdir -p out && cd out
  cmake -DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS=OFF -DCMAKE_C_FLAGS="-Ofast -m64 -march=native -mtune=native -flto -funroll-loops -ffunction-sections -fdata-sections -Wl,--gc-sections" -DCMAKE_CXX_FLAGS="-Ofast -m64 -march=native -mtune=native -flto -funroll-loops -ffunction-sections -fdata-sections -Wl,--gc-sections" -DCMAKE_INSTALL_PREFIX=./installed ..
  cmake --build . --config Release --target brotlienc
  popd >/dev/null

  # 用户检查
  grep -q "^${NGINX_GROUP}:" /etc/group || groupadd ${NGINX_GROUP}
  grep -q "^${NGINX_USER}:" /etc/passwd || useradd -g ${NGINX_GROUP} -M -s /sbin/nologin ${NGINX_USER}

  # 主程序编译
  rm -rf "nginx-${NGINX_VERSION}"
  tar xzf "nginx-${NGINX_VERSION}.tar.gz"
  pushd "nginx-${NGINX_VERSION}" >/dev/null

  sed -i 's@CFLAGS="$CFLAGS -g"@#CFLAGS="$CFLAGS -g"@' auto/cc/gcc
  export CFLAGS="-m64 -march=native -mtune=native -Ofast -flto -funroll-loops -ffunction-sections -fdata-sections -Wl,--gc-sections"
  export LDFLAGS="-m64 -Wl,-s -Wl,-Bsymbolic -Wl,--gc-sections"

  ./configure --prefix=${INSTALL_DIR} \
          --user=${NGINX_USER} --group=${NGINX_GROUP} \
          --with-http_stub_status_module --with-http_sub_module --with-http_v2_module \
          --with-http_ssl_module --with-http_flv_module --with-http_mp4_module \
          --with-http_gzip_static_module --with-http_realip_module --with-http_auth_request_module \
          --with-http_secure_link_module --with-threads --with-file-aio --with-stream \
          --with-stream_ssl_preread_module --with-stream_ssl_module \
          --add-module=../ngx_brotli \
          --add-module=../nginx-http-concat \
          --add-module=../ngx_cache_purge \
          --with-ld-opt='-ljemalloc'

  make -j$(nproc) && make install
  chown -R ${NGINX_USER}:${NGINX_GROUP} ${INSTALL_DIR}
  popd >/dev/null
}

post_install() {
  log "正在生成系统配置..."

  # 配置环境变量
  if ! grep -q "${INSTALL_DIR}/sbin" /etc/profile; then
    echo "export PATH=${INSTALL_DIR}/sbin:\$PATH" >> /etc/profile
  fi

  # 生成 Systemd Service
  cat > /usr/lib/systemd/system/nginx.service <<EOF
[Unit]
Description=Nginx HTTP Server
After=network.target

[Service]
Type=forking
PIDFile=${INSTALL_DIR}/logs/nginx.pid
ExecStartPre=${INSTALL_DIR}/sbin/nginx -t -c ${INSTALL_DIR}/conf/nginx.conf
ExecStart=${INSTALL_DIR}/sbin/nginx -c ${INSTALL_DIR}/conf/nginx.conf
ExecReload=${INSTALL_DIR}/sbin/nginx -s reload
ExecStop=${INSTALL_DIR}/sbin/nginx -s stop
ExecQuit=${INSTALL_DIR}/sbin/nginx -s quit
PrivateTmp=true

[Install]
WantedBy=multi-user.target
EOF

  # 配置文件
  mv ${INSTALL_DIR}/conf/nginx.conf{,_bk}
  /bin/cp "${CURRENT_DIR}"/nginx.conf ${INSTALL_DIR}/conf/nginx.conf
  cat > ${INSTALL_DIR}/conf/proxy.conf << EOF
proxy_redirect off;
proxy_hide_header Vary;
proxy_http_version 1.1;
proxy_set_header Host \$host;
proxy_set_header Connection '';
proxy_set_header Accept-Encoding '';
proxy_set_header User-Agent \$http_user_agent;
proxy_set_header Referer \$http_referer;
proxy_set_header Cookie \$http_cookie;
proxy_set_header X-Real-IP \$remote_addr;
proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
proxy_set_header X-Forwarded-Proto \$scheme;
proxy_connect_timeout 300s;
proxy_send_timeout 900;
proxy_read_timeout 900;
proxy_request_buffering on;
proxy_buffering on;
proxy_buffer_size 32k;
proxy_buffers 8 64k;
proxy_busy_buffers_size 128k;
EOF
  sed -i "s@^user www www@user ${NGINX_USER} ${NGINX_GROUP}@" ${INSTALL_DIR}/conf/nginx.conf

  # 修复 Logrotate 权限问题 (针对 Read-only file system 报错)
  mkdir -p /etc/systemd/system/logrotate.service.d/
  cat > /etc/systemd/system/logrotate.service.d/nginx-override.conf <<EOF
[Service]
ReadWritePaths=${INSTALL_DIR}/logs
EOF
  # 生成 Logrotate 配置
  cat > /etc/logrotate.d/nginx <<EOF
${INSTALL_DIR}/logs/*.log {
  daily
  rotate 5
  missingok
  compress
  delaycompress
  notifempty
  create 0640 ${NGINX_USER} ${NGINX_GROUP}
  sharedscripts
  postrotate
      [ -f ${INSTALL_DIR}/logs/nginx.pid ] && kill -USR1 \$(cat ${INSTALL_DIR}/logs/nginx.pid)
  endscript
}
EOF

  systemctl daemon-reload
  systemctl enable nginx
  log "Nginx 安装成功！可通过 systemctl start nginx 启动"
}

main() {
  prepare_env
  prepare_sources
  install_jemalloc
  install_nginx
  post_install
}

main "$@"