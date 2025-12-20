package com.govacode.designpatterns.creational.builder.v2;

import java.util.Set;

/**
 * 英雄分路
 *
 * @author gova
 */
public class Position {

    // 推荐分路
    private PositionEnum recommendedPosition;

    // 可选分路
    private Set<PositionEnum> optionalPositions;

    public PositionEnum getRecommendedPosition() {
        return recommendedPosition;
    }

    public void setRecommendedPosition(PositionEnum recommendedPosition) {
        this.recommendedPosition = recommendedPosition;
    }

    public Set<PositionEnum> getOptionalPositions() {
        return optionalPositions;
    }

    public void setOptionalPositions(Set<PositionEnum> optionalPositions) {
        this.optionalPositions = optionalPositions;
    }

    @Override
    public String toString() {
        return "Position{" +
                "recommendedPosition=" + recommendedPosition +
                ", optionalPositions=" + optionalPositions +
                '}';
    }
}
