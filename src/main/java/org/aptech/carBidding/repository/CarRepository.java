package org.aptech.carBidding.repository;

import org.aptech.carBidding.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    /**
     * Fetch all cars owned by the user with the given email.
     */
    @Query("SELECT c FROM Car c WHERE c.owner.email = :email")
    List<Car> findByOwnerEmail(@Param("email") String email);

    /**
     * Search cars by make or model (case‑insensitive, partial matches).
     */
    @Query("""
        SELECT c
          FROM Car c
         WHERE LOWER(c.make)  LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(c.model) LIKE LOWER(CONCAT('%', :term, '%'))
        """)
    List<Car> searchByMakeOrModel(@Param("term") String term);

    // The following come for free from JpaRepository, shown here for clarity:
    @Override
    List<Car> findAll();

    @Override
    java.util.Optional<Car> findById(Long id);

    @Override
    <S extends Car> S save(S entity);

    @Override
    void deleteById(Long id);

}
