package org.xdi.oxd.badgemanager.qrcode.decorator;

/**
 * Implement this interface to create custom decorators
 */
public interface QRCodeDecorator<T> {

  public T decorate(T qrcode);
}
