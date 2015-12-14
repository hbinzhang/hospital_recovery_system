package com.ericsson.skt.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("baseDao")
@Transactional(propagation = Propagation.SUPPORTS)
public class BaseDao extends AbstractDao {
}
