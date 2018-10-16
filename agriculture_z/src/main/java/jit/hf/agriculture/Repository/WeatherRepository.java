package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.Weather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<Weather,Long>{
    Weather findOneById(Long id);
    Page<Weather> findAll(Pageable pageable);
    Page<Weather> findDistinctByUpTimeContaining(String upTime,Pageable pageable);
}
