package com.usi.ch.syn.graphqlserver.dto;

import com.usi.ch.syn.core.model.Entity;
import com.usi.ch.syn.core.model.version.FileVersion;
import com.usi.ch.syn.core.model.view.MusicSheet;
import com.usi.ch.syn.core.model.view.View;
import com.usi.ch.syn.core.model.view.ViewAnimation;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
public class ViewDTO {
    private final int animationsCount;
    private final List<ViewAnimation> viewAnimationList;
    private final List<MusicSheet> musicSheets;

    public ViewDTO(int animationsCount, List<ViewAnimation> viewAnimationList, List<MusicSheet> musicSheets) {
        this.animationsCount = animationsCount;
        this.viewAnimationList = viewAnimationList;
        this.musicSheets = musicSheets;
    }

    public ViewDTO(View view) {
        this.animationsCount = view.getViewAnimationList().size();
        this.musicSheets = view.getMusicSheets();
        this.viewAnimationList = view.getViewAnimationList();
    }
}
