package campsite.reservations.application.persistence.reservation;

import campsite.reservations.core.domain.Reservation;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReservationMapper {

    public static Reservation fromEntity(final ReservationEntity entity) {
        if (entity == null) return null;
        return Reservation.builder()
                .status(entity.getStatus())
                .id(entity.getId())
                .customerEmail(entity.getCustomerEmail())
                .customerName(entity.getCustomerName())
                .checkInDate(entity.getCheckInDate())
                .checkOutDate(entity.getCheckOutDate())
                .build();
    }

    public static ReservationEntity toEntity(final Reservation reservation) {
        return ReservationEntity.builder()
                .id(reservation.getId())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .customerEmail(reservation.getCustomerEmail())
                .customerName(reservation.getCustomerName())
                .status(reservation.getStatus())
                .build();
    }
}
