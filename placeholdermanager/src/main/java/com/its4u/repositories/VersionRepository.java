package com.its4u.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.its4u.models.Versions;

public interface VersionRepository extends CrudRepository<Versions,String>{

	List<Versions> findAllByProjectId(String projectId);
}
