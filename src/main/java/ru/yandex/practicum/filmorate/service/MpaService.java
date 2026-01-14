package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;
    private final MpaMapper mpaMapper;

    public MpaDto getMpaDtoById(int id) {
        ru.yandex.practicum.filmorate.model.Mpa mpa = mpaStorage.getMpaById(id);
        return mpaMapper.toDto(mpa);
    }

    public Collection<MpaDto> getAllDto() {
        Collection<ru.yandex.practicum.filmorate.model.Mpa> allMpa = mpaStorage.getAllMpa();
        return allMpa.stream()
                .map(mpaMapper::toDto)
                .collect(Collectors.toList());
    }

    public ru.yandex.practicum.filmorate.model.Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }

    public Collection<ru.yandex.practicum.filmorate.model.Mpa> getAll() {
        return mpaStorage.getAllMpa();
    }
}