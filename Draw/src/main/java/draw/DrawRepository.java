package draw;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="draws", path="draws")
public interface DrawRepository extends PagingAndSortingRepository<Draw, Long>{


}
