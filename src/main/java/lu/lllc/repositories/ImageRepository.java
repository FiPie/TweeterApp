package lu.lllc.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import lu.lllc.entities.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {

	@Query("SELECT i.id FROM Image i")
	List<Integer> getImageIndexes();
	
}
