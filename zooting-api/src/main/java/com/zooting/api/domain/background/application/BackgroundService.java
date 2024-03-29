package com.zooting.api.domain.background.application;

import com.zooting.api.domain.background.dto.response.BackgroundPageRes;
import com.zooting.api.domain.background.dto.response.BackgroundRes;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BackgroundService {
    BackgroundPageRes findAllBackgroundImg(Pageable pageable);
}
