package ca.uhn.fhir.jpa.dao.data;

import ca.uhn.fhir.jpa.entity.ResourceTable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
 * #%L
 * HAPI FHIR JPA Server
 * %%
 * Copyright (C) 2014 - 2018 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

public interface IResourceTableDao extends JpaRepository<ResourceTable, Long> {

	@Query("SELECT t.myId FROM ResourceTable t WHERE t.myDeleted IS NOT NULL")
	Slice<Long> findIdsOfDeletedResources(Pageable thePageable);

	@Query("SELECT t.myId FROM ResourceTable t WHERE t.myResourceType = :restype AND t.myDeleted IS NOT NULL")
	Slice<Long> findIdsOfDeletedResourcesOfType(Pageable thePageable, @Param("restype") String theResourceName);

	@Query("SELECT t.myId FROM ResourceTable t WHERE t.myId = :resid AND t.myResourceType = :restype AND t.myDeleted IS NOT NULL")
	Slice<Long> findIdsOfDeletedResourcesOfType(Pageable thePageable, @Param("resid") Long theResourceId, @Param("restype") String theResourceName);

	@Query("SELECT t.myId FROM ResourceTable t WHERE t.myIndexStatus IS NULL")
	Slice<Long> findUnindexed(Pageable thePageRequest);

	@Modifying
	@Query("UPDATE ResourceTable r SET r.myIndexStatus = null WHERE r.myResourceType = :restype")
	int markResourcesOfTypeAsRequiringReindexing(@Param("restype") String theResourceType);

}
