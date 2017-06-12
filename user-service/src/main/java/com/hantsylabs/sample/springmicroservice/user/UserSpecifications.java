/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hantsylabs.sample.springmicroservice.user;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 *
 * @author hantsy
 */
public class UserSpecifications {

    public static Specification<User> byKeyword(String keyword, String role, String active){
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                predicates.add(
                    cb.or(
                        cb.like(root.get(User_.email), "%" + keyword + "%"),
                        cb.like(root.get(User_.username), "%" + keyword + "%")
                    ));
            }

            if (StringUtils.hasText(role) && !"ALL".equals(role)) {

                ListJoin<User, String> roleJoin = root.join(User_.roles);
                predicates.add(cb.equal(roleJoin, role));
            }
            if (StringUtils.hasText(active)) {
                predicates.add(cb.equal(root.get(User_.active), Boolean.valueOf(active)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
