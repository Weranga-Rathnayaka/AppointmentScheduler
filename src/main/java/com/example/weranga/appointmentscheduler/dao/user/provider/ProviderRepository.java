package com.example.weranga.appointmentscheduler.dao.user.provider;

import com.example.weranga.appointmentscheduler.dao.user.CommonUserRepository;
import com.example.weranga.appointmentscheduler.entity.Work;
import com.example.weranga.appointmentscheduler.entity.user.provider.Provider;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProviderRepository extends CommonUserRepository<Provider> {

    List<Provider> findByWorks(Work work);

    @Query("select distinct p from Provider p inner join p.works w where w.targetCustomer = 'retail'")
    List<Provider> findAllWithRetailWorks();

    @Query("select distinct p from Provider p inner join p.works w where w.targetCustomer = 'corporate'")
    List<Provider> findAllWithCorporateWorks();
}
