package ru.varlamova.dao;

import ru.varlamova.entity.PingRequest;

public interface PingRequestDAO {

    void saveOrUpdate(PingRequest pingRequest);

    String getCountByUser(PingRequest pingRequest);

}
