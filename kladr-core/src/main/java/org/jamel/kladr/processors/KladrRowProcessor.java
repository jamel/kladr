package org.jamel.kladr.processors;

import org.jamel.dbf.processor.DbfRowProcessor;
import org.jamel.dbf.utils.DbfUtils;
import org.jamel.kladr.utils.KladrCodeUtils;

/**
 * @author Sergey Polovko
 */
public abstract class KladrRowProcessor implements DbfRowProcessor {

    @Override
    public void processRow(Object[] row) {
        byte[] code = (byte[]) row[2];

        // skip invalid values
        if (!KladrCodeUtils.isValid(code)) return;

        byte regionId = KladrCodeUtils.getRegionId(code);
        int districtId = KladrCodeUtils.getDistrictId(code);
        int cityId = KladrCodeUtils.getCityId(code);
        int countryId = KladrCodeUtils.getCountryId(code);

        byte[] name = DbfUtils.trimLeftSpaces((byte[]) row[0]);
        byte[] socr = DbfUtils.trimLeftSpaces((byte[]) row[1]);
        int index = DbfUtils.parseInt((byte[]) row[3]);

        readRow(code, regionId, districtId, cityId, countryId, name, socr, index);
    }

    public abstract void readRow(
            byte[] code,
            byte regionId, int districtId, int cityId, int countryId,
            byte[] name, byte[] socr, int index);
}
