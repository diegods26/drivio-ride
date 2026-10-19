package com.drivio.driver.application.port.out;

import com.drivio.driver.application.dto.CurrentDriver;

public interface CurrentDriverProvider {

    CurrentDriver getCurrentDriver();
}