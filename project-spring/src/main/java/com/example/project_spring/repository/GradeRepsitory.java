package com.example.project_spring.repository;

import com.example.project_spring.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepsitory extends JpaRepository<Grade, Integer> {

}
