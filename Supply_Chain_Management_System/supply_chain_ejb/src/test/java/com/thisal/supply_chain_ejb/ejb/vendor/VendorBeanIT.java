package com.thisal.supply_chain_ejb.ejb.vendor;

import com.thisal.supply_chain_core.model.ResponseModel;
import jakarta.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ArquillianExtension.class)
public class VendorBeanIT {

    @EJB
    private com.thisal.supply_chain_core.service.VendorService vendorBean;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "supply_chain_ejb-test.jar")
                .addPackages(true, "com.thisal.supply_chain_ejb")
                .addPackages(true, "com.thisal.supply_chain_core")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml")
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml");
    }

    @Test
    public void createVendor_shouldPersist_andReturnResponse() {
        assertNotNull(vendorBean, "VendorBean should be injected by the Container.");

        String email = "arquillian-it-" + System.currentTimeMillis() + "@example.com";
        ResponseModel response = vendorBean.createVendor("arquillian-it", email);

        assertNotNull(response);
        assertEquals("Vendor created Successfully!", response.getMessage());
    }

}