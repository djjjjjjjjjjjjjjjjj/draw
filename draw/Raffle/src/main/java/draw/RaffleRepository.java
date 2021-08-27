package draw;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="raffles", path="raffles")
public interface RaffleRepository extends PagingAndSortingRepository<Raffle, Long>{


}
