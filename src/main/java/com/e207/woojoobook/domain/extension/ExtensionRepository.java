package com.e207.woojoobook.domain.extension;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtensionRepository extends JpaRepository<Extension, Long>, ExtensionRepositoryCustom {
}
