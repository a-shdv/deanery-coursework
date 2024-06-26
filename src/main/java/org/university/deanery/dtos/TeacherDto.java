package org.university.deanery.dtos;

import lombok.Getter;
import org.university.deanery.models.Subject;
import org.university.deanery.models.Teacher;

public record TeacherDto(@Getter String lastName, @Getter String firstName, @Getter String patronymicName, @Getter Subject subject) {
    public TeacherDto(String lastName, String firstName, String patronymicName, Subject subject) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymicName = patronymicName;
        this.subject = subject;
    }

    public static Teacher toTeacher(TeacherDto teacherDto) {
        return Teacher.builder()
                .firstName(teacherDto.firstName)
                .lastName(teacherDto.lastName)
                .patronymicName(teacherDto.patronymicName())

                .build();
    }
}
