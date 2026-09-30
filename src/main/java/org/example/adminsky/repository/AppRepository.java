package org.example.adminsky.repository;

import org.example.adminsky.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

public interface AppRepository extends JpaRepository<App, Long> {
    List<App> findAllByIdIn(Collection<Long> ids);
}
