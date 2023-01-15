package com.usi.ch.syn.graphqlserver.dto;

import java.util.Set;

public record FileTypeMetricsDTO(String fileType, Set<String> metrics) { }
