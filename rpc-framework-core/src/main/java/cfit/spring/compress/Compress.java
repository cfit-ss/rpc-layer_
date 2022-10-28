package cfit.spring.compress;

import cfit.fbs.extension.SPI;

/**
 * @author shengshuo.
 *
 */

@SPI
public interface Compress {

    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
