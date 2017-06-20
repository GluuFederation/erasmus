package org.xdi.oxd.badgemanager.qrcode.exception;

public class UnreadableDataException extends RuntimeException {

  public UnreadableDataException(String msg, Throwable cause){
    super(msg, cause);
  }

  public UnreadableDataException(String msg) {
    super(msg);
  }
}
