package com.example.shopappbackend.dto;

import jakarta.validation.constraints.NotEmpty;

import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private ModelMapper modelMapper;

    @NotEmpty(message = "Tên thể loại không được phép để trống")
    private String name;
}
