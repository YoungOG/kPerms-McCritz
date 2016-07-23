package com.mccritz.kperms.ranks;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankOptions {

    private RankType type;
    private long duration;

    public RankOptions(RankType type) {
        if (type != RankType.PERMENANT) {
            throw new IllegalArgumentException("A duration must be specified for temporary ranks!");
        }

        this.type = type;
    }

    public RankOptions(RankType type, long duration) {
        this.type = type;
        this.duration = duration;
    }
}
