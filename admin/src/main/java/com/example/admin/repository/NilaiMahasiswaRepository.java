package com.example.admin.repository;

import com.example.admin.dto.JadwalNilaiDto;
import com.example.admin.entity.NilaiMahasiswa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NilaiMahasiswaRepository extends JpaRepository<NilaiMahasiswa, Integer> {

    @Query("SELECT new com.example.admin.dto.JadwalNilaiDto(" +
           "k.idKegiatan, j.deadline, k.namaKegiatan, " +
           "(SELECT nm.nilai FROM NilaiMahasiswa nm JOIN nm.nilaiKelompok nk2 WHERE nk2.kegiatan = k AND nm.npm = :npm), " +
           "(SELECT nm.keterangan FROM NilaiMahasiswa nm JOIN nm.nilaiKelompok nk2 WHERE nk2.kegiatan = k AND nm.npm = :npm)) " +
           "FROM Kegiatan k " +
           "JOIN k.jadwal j " +
           "WHERE j.idTubes = :idTubes " +
           "ORDER BY j.deadline ASC, k.idKegiatan ASC")
    List<JadwalNilaiDto> findJadwalDanNilai(@Param("npm") String npm, 
                                            @Param("idTubes") Integer idTubes);

       @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("DELETE FROM NilaiMahasiswa nm WHERE nm.idNilaiKelompok IN :ids")
    void deleteByIdNilaiKelompokIn(@Param("ids") List<Integer> ids);

    NilaiMahasiswa findByNpmAndIdNilaiKelompok(String npm, Integer idNilaiKelompok);
}