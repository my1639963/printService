package com.example.printservice.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.printservice.entity.Printer;

@Mapper
public interface PrinterMapper extends BaseMapper<Printer> {
} 