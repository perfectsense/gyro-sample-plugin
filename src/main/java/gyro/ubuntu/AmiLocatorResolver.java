package gyro.ubuntu;

import gyro.core.GyroException;
import gyro.core.Type;
import gyro.core.reference.ReferenceResolver;
import gyro.core.scope.Scope;
import org.apache.commons.io.IOUtils;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

@Type("ubuntu-ami-locator")
public class AmiLocatorResolver extends ReferenceResolver {

    @Override
    public Object resolve(Scope scope, List<Object> arguments) throws Exception {

        if (arguments.size() != 4) {
            throw new GyroException("ubuntu-ami-locator needs 4 arguments - 'name', 'architecture', 'instance-type' and 'region'");
        }

        String zone = (String) arguments.get(0);
        String name = (String) arguments.get(1);
        String arch = (String) arguments.get(2);
        String type = (String) arguments.get(3);

        JSONObject json = new JSONObject(IOUtils.toString(new URL("https://cloud-images.ubuntu.com/locator/ec2/releasesTable"), StandardCharsets.UTF_8));
        JSONArray array = json.getJSONArray("aaData");

        HashSet<UbuntuAmiMapper> amiMappers = new HashSet<>();

        for (int i = 0; i< array.length(); i++) {
            JSONArray jsonArray = array.getJSONArray(i);
            UbuntuAmiMapper amiMapper = new UbuntuAmiMapper();
            setUbuntuMapper(amiMapper, jsonArray);
            amiMappers.add(amiMapper);
        }

        return amiMappers.stream()
            .filter(o -> o.getZone().equals(zone)
                && o.getName().equals(name)
                && o.getArchitecture().equals(arch)
                && o.getType().equals(type))
            .map(UbuntuAmiMapper::getAmi).findFirst().orElse(null);
    }

    private void setUbuntuMapper(UbuntuAmiMapper amiMapper, JSONArray jsonArray) throws JSONException {
        amiMapper.setZone(jsonArray.getString(0));
        amiMapper.setName(jsonArray.getString(1));
        amiMapper.setVersion(jsonArray.getString(2));
        amiMapper.setArchitecture(jsonArray.getString(3));
        amiMapper.setType(jsonArray.getString(4));
        amiMapper.setRelease(jsonArray.getString(5));
        amiMapper.setAmi("ami-" + jsonArray.getString(6).split(">ami-")[1].split("</a>")[0]);
    }
}
