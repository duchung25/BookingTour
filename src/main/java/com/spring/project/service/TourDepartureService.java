package com.spring.project.service;

import com.spring.project.dto.TourDepartureRequest;
import com.spring.project.entity.TourDeparture;

import java.util.List;

/**
 * Service quản lý lịch khởi hành — UC Admin 2.5.
 */
public interface TourDepartureService {

    /** Lấy tất cả departures của 1 tour, sắp xếp theo ngày đi */
    List<TourDeparture> getDeparturesByTourId(Long tourId);

    /** Lấy departure theo ID */
    TourDeparture getDepartureById(Long id);

    /** Thêm departure mới */
    void addDeparture(TourDepartureRequest request);

    /** Cập nhật departure */
    void updateDeparture(Long id, TourDepartureRequest request);

    /** Xóa departure (hard delete, chỉ khi không có booking) */
    void deleteDeparture(Long id);
}
