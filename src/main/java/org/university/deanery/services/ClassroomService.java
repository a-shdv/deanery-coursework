package org.university.deanery.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.university.deanery.dtos.ClassroomDto;
import org.university.deanery.exceptions.ClassroomNotFoundException;
import org.university.deanery.models.Classroom;
import org.university.deanery.repositories.ClassroomRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {
    private final ClassroomRepository classroomRepository;

    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    public void save(Classroom classroom) {
        classroomRepository.save(classroom);
    }

    public List<Classroom> findAll() {
        return classroomRepository.findAll();
    }

    public Page<Classroom> findAll(PageRequest pageRequest) {
        return classroomRepository.findAll(pageRequest);
    }


    public Optional<Classroom> findById(Long id) {
        return Optional.ofNullable(classroomRepository.findById(id)).get();
    }

    public Optional<Classroom> findClassroomByClassroomNo(Integer classroomNo) {
        return Optional.ofNullable(classroomRepository.findClassroomByClassroomNo(classroomNo)).get();
    }
    public void updateById(Long id, ClassroomDto classroomDto) throws ClassroomNotFoundException {
        Classroom classroom = classroomRepository.findById(id).get();
        classroom.setClassroomNo(classroomDto.getClassroomNo());
        classroomRepository.save(classroom);
    }

    public void delete(Classroom classroom) {
        classroomRepository.delete(classroom);
    }
}
