package com.merendero.facil.repository.specifications;

import com.merendero.facil.entities.DonationEntity;
import com.merendero.facil.entities.MerenderoEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Clase que provee una Specification dinámica para buscar entidades DonationEntity
 * La Specification se puede combinar con paginación y ordenamiento en el repositorio.
 **/
public class DonationSpecifications {

    /**
     * Crea una Specification dinámica para filtrar donaciones por merendero, email y rango de fechas.
     */
    public static Specification<DonationEntity> donationSearch(Long merenderoId,
                                                               LocalDate sinceDate,
                                                               LocalDate untilDate,
                                                               String email) {
        return (root, query, cb) -> {
            // root: la entidad DonationEntity
            // query: objeto CriteriaQuery que define el SELECT, JOINs, ORDER BY, etc
            // criteriaBuilder: fábrica de predicates y expresiones (AND, OR, LIKE). El where.

            Predicate predicate = cb.conjunction();

            // Filtro por merendero
            Join<DonationEntity, MerenderoEntity> joinMerendero = root.join("merendero");
            predicate = cb.and(predicate, cb.equal(joinMerendero.get("id"), merenderoId));

            // Filtro opcional por donor email
            if (email != null && !email.isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("userEmail")), "%" + email.toLowerCase() + "%"));
            }

            // Filtro opcional de fecha "desde" y "hasta"
            if (sinceDate != null && untilDate != null) {
                LocalDateTime start = sinceDate.atStartOfDay();
                LocalDateTime end = untilDate.atTime(LocalTime.MAX); // 23:59:59
                predicate = cb.and(predicate, cb.between(root.get("donationDate"), start, end));
            }

            return predicate;
        };
    }
}
