package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@Component
public class MpaMapper {

    public MpaDto toDto(Mpa mpa) {
        if (mpa == null) return null;

        MpaDto dto = new MpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getCode());
        return dto;
    }

    public Mpa toEntity(MpaDto dto) {
        if (dto == null) return null;

        Mpa mpa = new Mpa();
        mpa.setId(dto.getId());
        mpa.setCode(dto.getName());
        return mpa;
    }
}