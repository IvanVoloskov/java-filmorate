package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/{id}")
    public MpaDto getMpaById(@PathVariable Integer id) {
        return mpaService.getMpaDtoById(id);
    }

    @GetMapping
    public Collection<MpaDto> getAllMpa() {
        return mpaService.getAllDto();
    }
}