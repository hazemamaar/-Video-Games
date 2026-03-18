package com.example.videogames.data.mapper

import com.example.videogames.core.mapper.BaseMapper
import com.example.videogames.data.local.entity.GenreEntity
import com.example.videogames.data.remote.dto.GenreDto
import com.example.videogames.domain.model.Genre

class GenreDtoToGenreEntityMapper: BaseMapper<GenreDto,GenreEntity>(){
    override fun from(from: GenreDto): GenreEntity {
        return GenreEntity(
            id = from.id,
            name = from.name,
            slug =from.slug
        )
    }
}
class GenreEntityToGenreMapper: BaseMapper<GenreEntity,Genre>(){
    override fun from(from: GenreEntity): Genre {
        return Genre(
            id = from.id,
            name = from.name,
            slug =from.slug
        )
    }
}


