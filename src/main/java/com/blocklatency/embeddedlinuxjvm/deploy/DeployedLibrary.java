package com.blocklatency.embeddedlinuxjvm.deploy;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class DeployedLibrary {
    private String jarName;
    private String size;
    private String lastModified;
}
