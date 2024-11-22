package com.stc.mapper;

import com.stc.dto.EmployeeReq;
import com.stc.dto.EmployeeRes;
import com.stc.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "id", source = "id")
    EmployeeRes toEmployeeRes(Employee employee);

    @Mapping(target = "id", ignore = true)
    Employee toEntity(EmployeeReq request);
}