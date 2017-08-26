package com.skv.ru.bluetootharduino;

/**
 * Created by Константин on 29.05.2017.
 */

interface Communicator {
    void startCommunication();
    void write(String message);
    void stopCommunication();
}
