package com.govacode.designpatterns.creational.builder.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

/**
 * 建造者模式
 *
 * @author gova
 * @see StringBuilder
 * @see StringBuffer
 * @see java.nio.ByteBuffer
 * @see Appendable
 * @see java.net.http.HttpClient
 * @see java.util.Calendar
 * @see cn.hutool.core.thread.ThreadFactoryBuilder
 * @see org.springframework.web.util.UriComponentsBuilder
 *
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws InterruptedException, IOException {
        Position position = new Position();
        position.setRecommendedPosition(PositionEnum.JUNGLE);
        position.setOptionalPositions(Set.of(PositionEnum.TOP, PositionEnum.MIDDLE));
        Hero hero = Hero.newBuilder()
                .name("盲僧")
                .professions(Set.of(Profession.WARRIOR, Profession.ASSASSIN))
                .position(position)
                .skins(Set.of(new Skin("泳池派对", ""), new Skin("至高之拳", "")))
                .build();
        log.info("hero: {}", hero);

        // UriComponentsBuilder 应首先考虑使用fromXxx工厂方法创建
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("item.jd.com")
                .path("100066896356.html")
                .build();

        // JDK 11 HttpClient
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        /*httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println);*/
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("statusCode: {}, body: \n{}", response.statusCode(), response.body());

        // JDK 1.8 Calendar 改进
        Calendar calendar = new Calendar.Builder()
                .setDate(2000, 1, 1)
                .setTimeOfDay(12, 0, 0, 0)
                .setTimeZone(TimeZone.getDefault())
                .build();
        log.info("calendar: {}", calendar);

        // Dubbo URLBuilder ServiceBuilder
    }
}
