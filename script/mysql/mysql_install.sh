#!/bin/bash
current_dir=$(pwd)
mysql_package=Percona-Server-8.4.7-7-Linux.x86_64.glibc2.35.tar.gz
mysql_package_name=$(echo "${mysql_package}" | sed -r 's/\.tar\.(gz|xz)//')
mysql_main_version=$(echo "${mysql_package}" | grep -oE '[0-9]+\.[0-9]+' | head -1)
if [[ "${mysql_package}" =~ "Percona" ]]; then
  is_percona=1
else
  is_percona=0
fi
mysql_run_user=mysql
mysql_run_group=mysql
mysql_software_path=/usr/local
mysql_base_dir=${mysql_software_path}/mysql
jemalloc_version=5.3.0
os_name=$(grep "^NAME=" /etc/os-release | awk -F = '{print $2}' | sed 's/\"//g')
cpus=$(lscpu | grep -i 'CPU(s)' | sed -n '1p' | awk '{print $NF}')

log() { echo -e "$(date '+%Y-%m-%d %H:%M:%S') [\033[32mINFO\033[0m] ${*}"; }
log_warn() { echo -e "$(date '+%Y-%m-%d %H:%M:%S') [\033[33mWARN\033[0m] ${*}"; }
log_error() { echo -e "$(date '+%Y-%m-%d %H:%M:%S') [\033[1;31mERROR\033[0m] ${*}"; exit 1; }

check_numa() {
    if command -v numactl >/dev/null; then
        numa_nodes=$(numactl --hardware | grep 'nodes' | awk '{print $2}')
        if [ "$numa_nodes" -gt 1 ]; then
            log "检测到多 NUMA 节点, 将在 systemd 中启用 numactl 交错策略"
            NUMA_EXEC="numactl --interleave=all "
        fi
    fi
}

print_os_info() {
  machine_physics_net=$(ls /sys/class/net/ | grep -v "$(ls /sys/devices/virtual/net/)");
  mysql_report_host=$(ip addr | grep "${machine_physics_net}" | awk '/^[0-9]+: / {}; /inet.*global/ {print gensub(/(.*)\/(.*)/, "\\1", "g", $2)}' | head -1);
  log "本机IP: ${mysql_report_host}"

  os_type=$(cat /proc/sys/kernel/ostype)
  kernel_version=$(uname -r)
  platform=$(uname -m)
  os_version=$(grep "^VERSION_ID=" /etc/os-release  | awk -F = '{print $2}' | sed 's/\"//g')
  log "操作系统类型: ${os_type} 内核版本: ${kernel_version} 平台: ${platform} 操作系统: ${os_name}-${os_version}"

  cpu_model_name=$(lscpu | grep -i 'Model name' | awk -F : '{print $2}' | awk '{print $1}')
  cpu_sockets=$(lscpu | grep -i 'Socket(s)' | awk '{print $NF}')
  cpu_cores_per_socket=$(lscpu | grep -i 'Core(s) per socket' | awk '{print $NF}')
  cpu_threads_per_core=$(lscpu | grep -i 'Thread(s) per core' | awk '{print $NF}')
  log "CPU型号: ${cpu_model_name} 物理CPU数量: ${cpu_sockets} 每个物理CPU的核心数: ${cpu_cores_per_socket} 每个CPU核心的线程数: ${cpu_threads_per_core} 逻辑CPU个数: ${cpus}"

  mem_total=$(free -h | grep Mem | awk '{print $2}')
  mem_free=$(free -h | grep Mem | awk '{print $3}')
  mem_available=$(free -h | grep Mem | awk '{print $7}')
  log "总内存: ${mem_total} 未使用过的内存: ${mem_free} 可用内存(包括buffer): ${mem_available}"

  [[ "${os_name}" != "Ubuntu" && "${os_name}" != "CentOS Linux" ]] && log_error "❌ 不支持的操作系统"
}

os_optimize() {
  log "优化系统限制与内核参数..."
  if [[ "${os_name}" == "Ubuntu" ]]; then
    [ -e /etc/security/limits.d/*nproc.conf ] && rename nproc.conf nproc.conf_bk /etc/security/limits.d/*nproc.conf
    [ -z "$(grep 'session required pam_limits.so' /etc/pam.d/common-session)" ] && echo "session required pam_limits.so" >> /etc/pam.d/common-session
    sed -i '/^# End of file/,$d' /etc/security/limits.conf
    cat >> /etc/security/limits.conf <<EOF
# End of file
* soft nproc 1000000
* hard nproc 1000000
* soft nofile 1000000
* hard nofile 1000000
root soft nproc 1000000
root hard nproc 1000000
root soft nofile 1000000
root hard nofile 1000000
EOF
  else
    [ -e /etc/security/limits.d/*nproc.conf ] && rename nproc.conf nproc.conf_bk /etc/security/limits.d/*nproc.conf
    sed -i '/^# End of file/,$d' /etc/security/limits.conf
    cat >> /etc/security/limits.conf <<EOF
# End of file
* soft nproc 1000000
* hard nproc 1000000
* soft nofile 1000000
* hard nofile 1000000
EOF
  fi

  if [[ "${os_name}" == "Ubuntu" ]]; then
    [ -z "$(grep 'fs.file-max' /etc/sysctl.conf)" ] && cat >> /etc/sysctl.conf << EOF
vm.swappiness = 0
fs.file-max = 1000000
fs.inotify.max_user_instances = 8192
net.core.netdev_max_backlog = 10000
net.core.somaxconn = 32768
net.ipv4.ip_local_port_range = 1024 65000
net.ipv4.tcp_max_syn_backlog = 16384
net.ipv4.tcp_max_tw_buckets = 6000
net.ipv4.tcp_tw_reuse = 1
net.ipv4.tcp_syncookies = 1
net.ipv4.tcp_fin_timeout = 30
net.ipv4.route.gc_timeout = 100
net.ipv4.tcp_syn_retries = 1
net.ipv4.tcp_synack_retries = 1
net.ipv4.tcp_timestamps = 0
net.ipv4.tcp_max_orphans = 32768
EOF
  else
    [ ! -e "/etc/sysctl.conf_bk" ] && /bin/mv /etc/sysctl.conf{,_bk}
    cat > /etc/sysctl.conf << EOF
vm.swappiness = 0
fs.file-max = 1000000
net.core.wmem_default = 8388608
net.core.rmem_default = 8388608
net.core.rmem_max = 16777216
net.core.wmem_max = 16777216
net.core.netdev_max_backlog = 10000
net.core.somaxconn = 32768
net.ipv4.ip_local_port_range = 1024 65000
net.ipv4.tcp_max_syn_backlog = 16384
net.ipv4.tcp_max_tw_buckets = 6000
net.ipv4.tcp_tw_reuse = 1
net.ipv4.tcp_sack = 1
net.ipv4.tcp_window_scaling = 1
net.ipv4.tcp_rmem = 4096 87380 4194304
net.ipv4.tcp_wmem = 4096 16384 4194304
net.ipv4.tcp_mem = 94500000 915000000 927000000
net.ipv4.tcp_timestamps = 0
net.ipv4.tcp_fin_timeout = 20
net.ipv4.tcp_synack_retries = 2
net.ipv4.tcp_syn_retries = 2
net.ipv4.tcp_syncookies = 1
net.ipv4.tcp_max_orphans = 3276800
net.nf_conntrack_max = 6553500
net.netfilter.nf_conntrack_max = 6553500
net.netfilter.nf_conntrack_tcp_timeout_close_wait = 60
net.netfilter.nf_conntrack_tcp_timeout_fin_wait = 120
net.netfilter.nf_conntrack_tcp_timeout_time_wait = 120
net.netfilter.nf_conntrack_tcp_timeout_established = 3600
EOF
  fi
  sysctl -p
  log "✅ 系统限制与内核参数优化完成"
}

check_port_occupied() {
  log "正在检测端口占用: 业务[${mysql_port}], 管理[${mysql_admin_port}]..."

  local occupied_ports=()
  for port in ${mysql_port} ${mysql_admin_port}; do
    if ss -Hnlpt | grep -qw ":${port}"; then
        occupied_ports+=("$port")
    fi
  done

  if [ ${#occupied_ports[@]} -ne 0 ]; then
    log_error "❌ 端口冲突, 以下端口已被占用: [ ${occupied_ports[*]} ]"
  fi

  log "✅ 端口校验通过"
}

check_mysql_package_and_version() {
  if [ ! -f ${current_dir}/${mysql_package} ] ; then
    log_error "❌ 安装包不存在于 $1 目录下，请检查安装包名称是否指定正确 ${mysql_package}"
  else
    log "安装包${mysql_package}存在，版本：${mysql_main_version}"
    case "${mysql_main_version}" in
        "8.0")
            log "✅ 确认为 MySQL 8.0 系列 (常规版本)"
            ;;
        "8.4")
            log "✅ 确认为 MySQL 8.4 系列 (LTS 版本)"
            ;;
        *)
            log_error "❌ 不支持的版本: [${mysql_main_version}], 本脚本仅深度适配 MySQL 8.0 和 8.4 LTS"
            ;;
    esac
  fi
}

uninstall_mariadb() {
  if [ "${os_name}" == "CentOS Linux" ]; then
    rpm -qa | grep mariadb >/dev/null
    if [ $? -eq 0 ] ; then
      mariadb=$(rpm -qa | grep mariadb)
      log "检测到系统自带的MariaDB: ${mariadb} 准备卸载..."
      yum erase -y "${mariadb}" >/dev/null
      rpm -qa | grep mariadb >/dev/null
      if [ $? -eq 1 ] ; then
        log "✅ MariaDB卸载完成"
      fi
    fi
  fi
}

install_mysql_dependency_packages() {
  log "开始安装依赖包..."
  centos_packages=("gcc" "gcc-c++" "make" "epel-release" "libaio" "autogen" "autoconf" "numactl")
  ubuntu_packages=("gcc" "g++" "make" "libaio1" "autogen" "autoconf" "numactl")
  if [[ "$os_name" == "CentOS Linux" ]]; then
      yum install -q -y "${centos_packages[@]}" >/dev/null
  else
      apt install -q -y "${ubuntu_packages[@]}" >/dev/null
  fi

  if ldconfig -p | grep -q jemalloc; then
      log "jemalloc 已安装"
  else
    log "开始安装jemalloc-${jemalloc_version}..."
    if [ ! -f ${current_dir}/jemalloc-${jemalloc_version}.tar.bz2 ] ; then
      log_error "jemalloc-${jemalloc_version} 安装包不存在"
    fi

    tar -jxf jemalloc-${jemalloc_version}.tar.bz2 > /dev/null
    pushd jemalloc-${jemalloc_version} > /dev/null
    ./autogen.sh
    make -j2 && make install
    popd > /dev/null

    if [ -f "/usr/local/lib/libjemalloc.so" ]; then
      [ -z "`grep /usr/local/lib /etc/ld.so.conf.d/*.conf`" ] && echo '/usr/local/lib' > /etc/ld.so.conf.d/local.conf
      ldconfig
      rm -rf jemalloc-${jemalloc_version}
      if ldconfig -p | grep -q jemalloc; then
        log "✅ jemalloc-${jemalloc_version} 安装完成"
      else
        log_error "❌ jemalloc-${jemalloc_version} 安装失败"
      fi
    else
      rm -rf jemalloc-${jemalloc_version}
      log_error "❌ jemalloc-${jemalloc_version} 安装失败"
    fi
  fi
  log "✅ 依赖包安装完成"
}

check_mysql_data_dir() {
  if [ -d ${mysql_data_dir} ]; then
    log_error "❌ ${mysql_data_dir} 目录已存在, 请检查并确认该目录数据"
  else
    log "创建MySQL数据目录 ${mysql_data_dir}"
    mkdir -p ${mysql_data_dir}
    log "✅ 数据目录创建完成"
  fi
}

check_mysql_user() {
  egrep "^${mysql_run_group}" /etc/group >& /dev/null
  if [ $? -ne 0 ] ; then
    log "添加 ${mysql_run_group} 用户组"
    groupadd ${mysql_run_group}
  else
    log "${mysql_run_group} 用户组已存在"
  fi

  egrep "^${mysql_run_user}" /etc/passwd >& /dev/null
  if [ $? -ne 0 ] ; then
    log "添加 ${mysql_run_user} 用户"
    useradd -r -g ${mysql_run_group} -s /bin/false ${mysql_run_user}
  else
    log "${mysql_run_user} 用户已存在"
  fi

  log "修改数据目录: ${mysql_data_dir} 属主为${mysql_run_user}"
  chown -R ${mysql_run_group}:${mysql_run_user} ${mysql_data_dir}
  log "修改数据目录: ${mysql_data_dir} 权限为750"
  chmod -R 750 ${mysql_data_dir}
  log "✅ 数据目录属主及权限设置完成"
}

decompress_mysql_package() {
  if [ ${mysql_multi_instances} -eq 1 ]; then
    log "单机多实例安装默认无需解压MySQL安装包"
  else
    log "解压MySQL安装包至: ${mysql_software_path}/${mysql_package_name}..."
    tar xf ${current_dir}/${mysql_package} -C ${mysql_software_path}
    [ -L ${mysql_base_dir} ] && rm -f ${mysql_base_dir}
    ln -s ${mysql_software_path}/${mysql_package_name} ${mysql_base_dir}
    log "✅ 安装包解压完成"
  fi
}

config_mysql_env_variable() {
  [ -z "$(grep ^'export PATH=' /etc/profile)" ] && echo "export PATH=${mysql_base_dir}/bin:\$PATH" >> /etc/profile
  [ -n "$(grep ^'export PATH=' /etc/profile)" -a -z "$(grep ${mysql_base_dir} /etc/profile)" ] && sed -i "s@^export PATH=\(.*\)@export PATH=${mysql_base_dir}/bin:\1@" /etc/profile
  . /etc/profile
  log "✅ 环境变量设置完成, 当前MySQL版本: `mysql -V`"
}

config_jemalloc() {
  log "配置使用jemalloc内存管理..."
  if [ -d /etc/sysconfig ]; then
    if [ ! -f /etc/sysconfig/mysql ]; then
      touch /etc/sysconfig/mysql && echo "LD_PRELOAD=/usr/local/lib/libjemalloc.so" >>/etc/sysconfig/mysql
      log "✅ jemalloc已写入/etc/sysconfig/mysql"
    else
      log "jemalloc内存管理 /etc/sysconfig/mysql 已存在"
    fi
  else
    mkdir /etc/sysconfig && touch /etc/sysconfig/mysql && echo "LD_PRELOAD=/usr/local/lib/libjemalloc.so" >>/etc/sysconfig/mysql
    log "✅ jemalloc已写入/etc/sysconfig/mysql"
  fi
}

config_systemd() {
  log "配置systemd (NUMA_EXEC: ${NUMA_EXEC:-none})..."
  if [ ! -f ${mysql_systemd_service_file} ]; then
    touch ${mysql_systemd_service_file}
    if [ ${mysql_multi_instances} -eq 0 ]; then
      cat >${mysql_systemd_service_file}  <<EOF
[Unit]
Description=MySQL Server
After=network.target
After=syslog.target
[Install]
WantedBy=multi-user.target
[Service]
User=${mysql_run_user}
Group=${mysql_run_group}
Type=notify
TimeoutSec=0
ExecStart=${NUMA_EXEC}${mysql_base_dir}/bin/mysqld --defaults-file=${mysql_config_file} \$MYSQLD_OPTS
EnvironmentFile=-/etc/sysconfig/mysql
LimitNOFILE=65536
Restart=on-failure
RestartPreventExitStatus=1
Environment=MYSQLD_PARENT_PID=1
PrivateTmp=false
EOF
    else
      cat >${mysql_systemd_service_file}  <<EOF
[Unit]
Description=MySQL Server
After=network.target
After=syslog.target
[Install]
WantedBy=multi-user.target
[Service]
User=${mysql_run_user}
Group=${mysql_run_group}
Type=notify
TimeoutSec=0
ExecStart=${NUMA_EXEC}${mysql_base_dir}/bin/mysqld --defaults-file=${mysql_config_file} --defaults-group-suffix=@%I \$MYSQLD_OPTS
EnvironmentFile=-/etc/sysconfig/mysql
LimitNOFILE=65536
Restart=on-failure
RestartPreventExitStatus=1
Environment=MYSQLD_PARENT_PID=1
PrivateTmp=false
EOF
    fi
    systemctl daemon-reload
    log "✅ systemd配置完成 ${mysql_systemd_service_file}"
  else
    log "${mysql_systemd_service_file}已存在"
  fi
}

write_config_file() {

  if [ ! -f ${mysql_config_file} ] ; then
    log "正在为 MySQL${mysql_main_version} 生成配置文件${mysql_config_file}..."
    cat >${mysql_config_file} << EOF
[client]
socket                                                      = ${mysql_socket_file}

[mysql]
loose-skip-binary-as-hex
no-auto-rehash
prompt                                                      = (\\u@\\h) [\\d]>\\_

[mysqldump]
single-transaction

[mysqld]
# ------------------------------ basic settings ------------------------------
report_host                                                 = ${mysql_report_host}
user                                                        = ${mysql_run_user}
port                                                        = ${mysql_port}
server_id                                                   = ${mysql_server_id}
basedir                                                     = ${mysql_base_dir}
datadir                                                     = ${mysql_data_dir}
socket                                                      = ${mysql_socket_file}
character_set_server                                        = utf8mb4
collation_server                                            = utf8mb4_general_ci
default_storage_engine                                      = InnoDB
transaction_isolation                                       = READ-COMMITTED
lower_case_table_names                                      = 0
autocommit                                                  = ON
explicit_defaults_for_timestamp                             = ON
event_scheduler                                             = ON
# sql_require_primary_key                                   = ON
default_time_zone                                           = "+8:00"
bind_address                                                = "0.0.0.0"
open_files_limit                                            = 65535
sql_mode                                                    = "ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION"
activate_all_roles_on_login                                 = ON
# ------------------------------ administrative interface ------------------------------
admin_address                                               = 127.0.0.1
admin_port                                                  = ${mysql_admin_port}
create_admin_listener_thread                                = ON
# ------------------------------ connection ------------------------------
interactive_timeout                                         = 1800
wait_timeout                                                = 1800
lock_wait_timeout                                           = 60
connect_timeout                                             = 10
skip_name_resolve                                           = ON
max_connections                                             = 1024
max_user_connections                                        = 256
max_connect_errors                                          = 1000000
back_log                                                    = 1024
max_allowed_packet                                          = 64M
# ------------------------------ table cache performance settings ------------------------------
table_open_cache                                            = 4096
table_definition_cache                                      = 4096
table_open_cache_instances                                  = 64
# ------------------------------ session memory settings ------------------------------
sort_buffer_size                                            = 4M
join_buffer_size                                            = 4M
read_buffer_size                                            = 8M
read_rnd_buffer_size                                        = 4M
bulk_insert_buffer_size                                     = 64M
tmp_table_size                                              = 96M
max_heap_table_size                                         = 96M
thread_cache_size                                           = 64
thread_stack                                                = 512K
EOF

    if [ ${is_percona} -eq 1 ]; then
      cat >> ${mysql_config_file} << EOF
# ------------------------------ thread pool ------------------------------
thread_handling                                             = pool-of-threads
thread_pool_size                                            = $[${cpus}*4]
EOF
    fi

    cat >> ${mysql_config_file} << EOF
# ------------------------------ log settings ------------------------------
log_output                                                  = FILE
general_log                                                 = OFF
general_log_file                                            = general.log
slow_query_log                                              = ON
log_slow_extra                                              = ON
slow_query_log_file                                         = slow.log
long_query_time                                             = 1
min_examined_row_limit                                      = 100
log_queries_not_using_indexes                               = ON
log_throttle_queries_not_using_indexes                      = 60 # 默认10 同一未使用索引的查询每十次才会记录一次
log_slow_admin_statements                                   = ON
log_slow_replica_statements                                 = ON
log_error                                                   = error.log
log_error_verbosity                                         = 3
log_timestamps                                              = system
log-bin                                                     = binlog
EOF

    if [ "${mysql_main_version}" == "8.0" ]; then
      cat >> ${mysql_config_file} << EOF
binlog_format                                               = ROW # 8.0.34 已废弃 默认ROW格式
EOF
    fi

    cat >> ${mysql_config_file} << EOF
binlog_rows_query_log_events                                = ON
binlog_row_image                                            = FULL
binlog_row_metadata                                         = FULL
binlog_checksum                                             = CRC32 # 8.0.20 及之前版本若要启用MGR需要设置为 NONE
sync_binlog                                                 = 1
binlog_cache_size                                           = 4M
max_binlog_size                                             = 1G
binlog_space_limit                                          = 500G
binlog_expire_logs_auto_purge                               = ON
binlog_expire_logs_seconds                                  = 604800 # binlog过期时间，默认2592000s(30*24*60*60) 8.0用于替换expire_logs_days参数
log_bin_trust_function_creators                             = ON
# ------------------------------ perforamnce_schema ------------------------------
performance_schema                                          = ON
performance-schema-instrument                               = lock=ON # wait/lock/metadata/sql/mdl=ON
# ------------------------------ MyISAM ------------------------------
key_buffer_size                                             = 64M
myisam_sort_buffer_size                                     = 8M
concurrent_insert                                           = 2
low_priority_updates                                        = 1
myisam_recover_options                                      = BACKUP,FORCE
# ------------------------------ InnoDB ------------------------------
innodb_page_size                                            = 16384
innodb_default_row_format                                   = dynamic
innodb_buffer_pool_size                                     = ${mysql_innodb_buffer_pool_size}G
innodb_buffer_pool_instances                                = $[${cpus}*2]
innodb_buffer_pool_chunk_size                               = 128M
innodb_buffer_pool_dump_at_shutdown                         = ON
innodb_buffer_pool_dump_pct                                 = 25
innodb_buffer_pool_load_at_startup                          = ON
innodb_old_blocks_pct                                       = 37
innodb_old_blocks_time                                      = 1000
innodb_read_ahead_threshold                                 = 56 # 0-64
innodb_random_read_ahead                                    = OFF
innodb_max_dirty_pages_pct_lwm                              = 10 # low water mark value
innodb_max_dirty_pages_pct                                  = 90
innodb_flush_method                                         = O_DIRECT
innodb_flush_neighbors                                      = ${mysql_innodb_flush_neighbors}
innodb_lru_scan_depth                                       = 4096
innodb_adaptive_flushing                                    = ON
innodb_adaptive_flushing_lwm                                = 10
innodb_flushing_avg_loops                                   = 30
innodb_io_capacity                                          = ${mysql_innodb_io_capacity}
innodb_io_capacity_max                                      = $[${mysql_innodb_io_capacity}*2]
innodb_adaptive_hash_index                                  = OFF
innodb_change_buffering                                     = all
innodb_log_buffer_size                                      = 64M
innodb_redo_log_capacity                                    = 8G # 8.0.30 取代 innodb_log_file_size 和 innodb_log_files_in_group 由32个文件组成
innodb_flush_log_at_trx_commit                              = 1
innodb_file_per_table                                       = ON
innodb_data_file_path                                       = ibdata1:12M:autoextend
innodb_rollback_on_timeout                                  = ON
innodb_strict_mode                                          = ON
innodb_deadlock_detect                                      = ON
innodb_lock_wait_timeout                                    = 10
innodb_print_all_deadlocks                                  = ON
innodb_print_lock_wait_timeout_info                         = ON
innodb_autoinc_lock_mode                                    = 2
innodb_numa_interleave                                      = OFF
innodb_use_native_aio                                       = ON
innodb_open_files                                           = 65534
innodb_stats_persistent                                     = ON
innodb_stats_persistent_sample_pages                        = 64
innodb_sort_buffer_size                                     = 64M
innodb_ddl_threads                                          = $[${cpus}*2]
innodb_ddl_buffer_size                                      = 128M
innodb_online_alter_log_max_size                            = 4G
innodb_print_ddl_logs                                       = ON
innodb_status_file                                          = ON
innodb_status_output                                        = OFF
innodb_status_output_locks                                  = ON
innodb_thread_concurrency                                   = $[${cpus}*2]
innodb_concurrency_tickets                                  = 5000
innodb_page_cleaners                                        = $[${cpus}*2]
innodb_read_io_threads                                      = $[${cpus}*2]
innodb_write_io_threads                                     = $[${cpus}*2]
innodb_purge_threads                                        = $[${cpus}*2]
innodb_parallel_read_threads                                = $[${cpus}*2]
# ------------------------------ replication ------------------------------
relay_log                                                   = relaylog
replica_net_timeout                                         = 10
relay_log_recovery                                          = ON
log_replica_updates                                         = ON # relaylog转储到binlog
gtid_mode                                                   = ON
enforce_gtid_consistency                                    = ON
EOF

    if [ "${mysql_main_version}" == "8.0" ]; then
      cat >> ${mysql_config_file} << EOF
master_info_repository                                      = TABLE # 8.0.23 已废弃
relay_log_info_repository                                   = TABLE # 8.0.23 已废弃
EOF
    fi

    cat >> ${mysql_config_file} << EOF
# semi sync replication
loose_plugin_load_add                                       = "semisync_source.so;semisync_replica.so"
loose_rpl_semi_sync_source_enabled                          = ON
loose_rpl_semi_sync_source_wait_for_replica_count           = 1
loose_rpl_semi_sync_source_wait_no_replica                  = ON
loose_rpl_semi_sync_source_timeout                          = 10000
loose_rpl_semi_sync_source_wait_point                       = AFTER_SYNC # 无损复制
loose_rpl_semi_sync_replica_enabled                         = ON
# parallel replication
replica_parallel_workers                                    = $[${cpus}*2]
replica_preserve_commit_order                               = ON
binlog_transaction_dependency_history_size                  = 25000
EOF

    if [ "${mysql_main_version}" == "8.0" ]; then
      cat >> ${mysql_config_file} << EOF
replica_parallel_type                                       = LOGICAL_CLOCK # 8.0.29 已废弃 默认LOGICAL_CLOCK
binlog_transaction_dependency_tracking                      = WRITESET # 8.0.35 已废弃
transaction_write_set_extraction                            = XXHASH64 # 8.0.26 已废弃
EOF
    fi

    cat >> ${mysql_config_file} << EOF
# group replication
loose_plugin_load_add                                       = "mysql_clone.so;group_replication.so"
# loose_group_replication_group_name                        = "3ebf2a95-227d-4f9e-a4d2-ca911ae46143"
# loose_group_replication_local_address                     = 127.0.0.1:13306
# loose_group_replication_group_seeds                       = 127.0.0.1:13306,127.0.0.1:13307,127.0.0.1:13308
# loose-group_replication_communication_stack               = "XCOM"
# loose_group_replication_start_on_boot                     = OFF # 是否在实例启动时自动开启组复制
# loose_group_replication_bootstrap_group                   = OFF # 是否是引导节点
# loose-group_replication_flow_control_mode                 = "DISABLED"
# loose_group_replication_single_primary_mode               = ON # 单主模式
# loose_group_replication_enforce_update_everywhere_checks  = OFF # 是否开启严格的一致性检查（多主模式下设置为ON）
# loose_group_replication_exit_state_action                 = READ_ONLY
# loose_group_replication_transaction_size_limit            = 150000000 # 默认143M事务大小，最大值2147483647（大约2G），当此系统变量设置为0时，该组接受的事务大小没有限制。
# loose_group_replication_recovery_get_public_key           = ON

[mysqld_safe]
malloc-lib                                                  = /usr/local/lib/libjemalloc.so
EOF
  else
    if [ ${mysql_multi_instances} -eq 1 ]; then
      log "单机多实例安装 向 ${mysql_config_file} 中追加多实例配置"
      cat >> ${mysql_config_file} << EOF

[mysqld@${mysql_port}]
port                                                        = ${mysql_port}
server_id                                                   = ${mysql_server_id}
datadir                                                     = ${mysql_data_dir}
socket                                                      = ${mysql_socket_file}
EOF
    else
      log_error "❌ 配置文件: ${mysql_config_file} 已存在"
    fi
  fi

  log "校验配置文件: ${mysql_config_file}..."
  ${mysql_base_dir}/bin/mysqld --defaults-file=${mysql_config_file} --validate-config
  if [ $? -ne 0 ]; then
    log_error "❌ 配置文件校验失败"
  else
    log "✅ 配置文件校验通过"
  fi
}

initialize_mysql() {
  log "初始化MySQL实例..."
  ${mysql_base_dir}/bin/mysqld --defaults-file=${mysql_config_file} --datadir=${mysql_data_dir} --initialize --user=${mysql_run_user}
  init_exit_code=$?
  if [[ ${init_exit_code} -eq 0 ]]; then
      log "✅ MySQL实例初始化成功"
  else
      log_error "❌ MySQL实例初始化失败, 退出码: ${init_exit_code}"
  fi
}

get_random_pwd() {
  local log_file="${mysql_data_dir}/error.log"
  local password=""
  local max_retry=5
  local retry_count=0

  log "正在尝试从日志中获取初始随机密码..."

  # 确保日志文件存在且可读
  while [ ! -f "${log_file}" ] || [ ! -s "${log_file}" ]; do
      ((retry_count++))
      if [ $retry_count -gt $max_retry ]; then
          log_error "无法找到初始化日志文件: ${log_file}，请检查初始化步骤是否成功。"
      fi
      log_warn "日志文件尚未生成，等待 2 秒... ($retry_count/$max_retry)"
      sleep 2
  done

  # 标准匹配 (针对 root@localhost: password)
  password=$(grep "temporary password" "${log_file}" | sed 's/.*root@localhost: //')

  # 容错匹配 (如果方案 A 拿到的结果为空，尝试更宽泛的正则)
  if [ -z "${password}" ]; then
      password=$(grep "A temporary password" "${log_file}" | awk '{print $NF}')
  fi

  if [ -n "${password}" ]; then
      password=$(echo "${password}" | tr -d '[:space:]')
      log "✅ 成功获取初始密码: ${password}"
      mysql_random_password="${password}"
  else
      log_error "❌ 无法从 ${log_file} 中提取初始密码"
  fi
}

start_mysql() {
  log "启动MySQL实例..."
  if [ ${mysql_multi_instances} -eq 0 ]; then
    systemctl start mysqld
  else
    systemctl start mysqld@${mysql_port}
  fi
}

check_mysql_ready() {
    local timeout=60
    local count=0
    
    log "等待MySQL实例启动..."
    
    while [ $count -lt $timeout ]; do
        if netstat -ntlp | grep -qw "${mysql_port}"; then
            if [ -S "${mysql_socket_file}" ]; then
                log "✅ MySQL已就绪, 耗时: ${count}s"
                return 0
            fi
        fi
        
        if [ ${mysql_multi_instances} -eq 0 ]; then
            systemctl is-failed --quiet mysqld && log_error "❌ MySQL启动失败, 请检查错误日志：${mysql_data_dir}/error.log"
        else
            systemctl is-failed --quiet mysqld@${mysql_port} && log_error "❌ MySQL多实例 @${mysql_port} 启动失败"
        fi

        sleep 2
        ((count+=2))
        log "MySQL启动中... (${count}s)"
    done

    log_error "❌ MySQL启动超时, 请手动检查错误日志: ${mysql_data_dir}/error.log"
}

modify_root_pwd() {
  log "设置MySQL基础用户密码并创建复制用户..."
  mysql -S ${mysql_socket_file} -p"${mysql_random_password}" --connect-expired-password <<EOF
ALTER USER 'root'@'localhost' IDENTIFIED BY '${mysql_password}';
CREATE USER 'root'@'%' IDENTIFIED BY '${mysql_password}';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';
CREATE USER 'repl'@'%' IDENTIFIED BY 'repl@123';
GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
FLUSH PRIVILEGES;
EOF
  log "✅ 设置MySQL基础用户密码完成"
}

auto_startup() {
  log "设置MySQL开机自启..."
  if [ ${mysql_multi_instances} -eq 0 ]; then
    systemctl enable mysqld
  else
    systemctl enable mysqld@${mysql_port}
  fi
  log "✅ 设置MySQL开机自启完成"
}

config_firewall() {
    log "检测防火墙状态..."
    
    if command -v firewall-cmd >/dev/null 2>&1; then
        if systemctl is-active --quiet firewalld; then
            log "Firewalld 正在运行，放行端口: ${mysql_port}"
            firewall-cmd --permanent --add-port=${mysql_port}/tcp >/dev/null 2>&1
            firewall-cmd --reload >/dev/null 2>&1
        else
            log_warn "Firewalld 已安装但未启动，跳过端口放行配置"
        fi
    elif command -v ufw >/dev/null 2>&1; then
        if ufw status | grep -qw "active"; then
            log "UFW 正在运行，放行端口: ${mysql_port}"
            ufw allow ${mysql_port}/tcp >/dev/null 2>&1
        else
            log_warn "UFW 处于关闭状态，跳过端口放行配置"
        fi
    else
        log "未检测到已知防火墙服务 (firewalld/ufw)，无需配置"
    fi
}

summary() {
  log "MySQL端口: ${mysql_port}"
  log "MySQL安装路径: ${mysql_base_dir}"
  log "MySQL数据目录: ${mysql_data_dir}"
  log "MySQL配置文件: ${mysql_config_file}"
}

usage() {
    cat << EOF
用法: $0 [选项]

选项:
  -p <port>             MySQL 端口
  -s <server_id>        MySQL Server ID
  -w <password>         root 用户密码
  -b <size>             InnoDB 缓冲池大小(单位 GB)
  -i <iops>             磁盘 IOPS
  -m <multi_instances>  是否单机多实例安装(0-否 1-是)
  -h                    显示此帮助信息

示例:
  bash $0 -p 3306 -s 1 -w root@123 -b 4 -i 2000
EOF
    exit 0
}

parse_args() {
  [[ $# -eq 0 ]] && usage

  mysql_multi_instances=0

  while getopts ":p:s:w:b:i:m:h" opt; do
    case $opt in
      p) mysql_port="$OPTARG" ;;
      s) mysql_server_id="$OPTARG" ;;
      w) mysql_password="$OPTARG" ;;
      b) mysql_innodb_buffer_pool_size="$OPTARG" ;;
      i) mysql_innodb_io_capacity="$OPTARG" ;;
      m) mysql_multi_instances="$OPTARG" ;;
      h) usage ;;
      :) log_error "选项 -$OPTARG 需要一个参数." ;;
      ?) log_error "无效的选项: -$OPTARG, 使用 -h 查看可用选项" ;;
    esac
  done

  if [ -z "$mysql_port" ] || [ -z "$mysql_server_id" ] || [ -z "$mysql_password" ] || [ -z "$mysql_innodb_buffer_pool_size" ] || [ -z "$mysql_innodb_io_capacity" ]; then
    log_error "用法: $0 -p <port> -s <server_id> -w <password> -b <buffer_pool_size G> -i <io_capacity> -m <multi_instances>"
  fi

  log "端口: ${mysql_port}"
  log "Server ID: ${mysql_server_id}"
  log "root 用户密码: ${mysql_password}"
  log "InnoDB 缓冲池大小: ${mysql_innodb_buffer_pool_size} GB"
  log "磁盘IOPS: ${mysql_innodb_io_capacity}"
  log "是否单机多实例: ${mysql_multi_instances}"
}

init_env() {
  mysql_admin_port="${mysql_port}2"
  mysql_root=/data/mysql
  mysql_data_dir=${mysql_root}/${mysql_port}
  mysql_config_file=/etc/my.cnf
  mysql_socket_file=${mysql_data_dir}/mysql.sock
  mysql_random_password=""

  if [ ${mysql_innodb_io_capacity} -le 2000 ]; then mysql_innodb_flush_neighbors=1; else mysql_innodb_flush_neighbors=0; fi;
  if [ ${mysql_multi_instances} -eq 0 ]; then
    mysql_systemd_service_file=/usr/lib/systemd/system/mysqld.service
  else
    mysql_systemd_service_file=/usr/lib/systemd/system/mysqld@.service
  fi
}

main() {
  print_os_info
  parse_args "$@"
  init_env
  check_mysql_package_and_version
  os_optimize
  check_port_occupied
  check_numa
  uninstall_mariadb
  install_mysql_dependency_packages
  check_mysql_data_dir
  check_mysql_user
  decompress_mysql_package
  config_mysql_env_variable
  config_jemalloc
  config_systemd
  write_config_file
  initialize_mysql
  get_random_pwd
  start_mysql
  check_mysql_ready
  modify_root_pwd
  auto_startup
  config_firewall
  summary
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi