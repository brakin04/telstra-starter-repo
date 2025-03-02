package au.com.telstra.simcardactivator.repository;

import au.com.telstra.simcardactivator.model.ActivationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationRecordRepository extends JpaRepository<ActivationRecord, Long> {
}
