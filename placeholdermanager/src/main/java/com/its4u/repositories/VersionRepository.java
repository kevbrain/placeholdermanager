package com.its4u.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.its4u.models.Versions;
import com.its4u.models.VersionsId;

public interface VersionRepository extends CrudRepository<Versions,VersionsId>{


}
