package io.pivotal.service;

import io.pivotal.domain.Customer;
import io.pivotal.util.CustomerFactory;

import java.util.HashMap;
import java.util.Map;

import com.gemstone.gemfire.cache.Region;

public class CustomerLoader {

	private final int BATCH_SIZE = 10000;
	private final int SAMPLE_SIZE = 100000;
	
	Region<Integer, Customer> customerRegion = null;
	CustomerFactory customerFactory = null;
	
	public CustomerLoader(Region<Integer, Customer> customerRegion) {
		this.customerRegion = customerRegion;
		this.customerFactory = new CustomerFactory();
	}
	
	public void preLoad() {
		Map<Integer, Customer> buffer = new HashMap<Integer, Customer>();

		for (int i = 1; i < SAMPLE_SIZE + 1; i++) {
			Customer customer = customerFactory.generateCustomer(i);
			buffer.put(i, customer);

			if ((i % BATCH_SIZE) == 0) {
				// ready to insert a batch into region
				customerRegion.putAll(buffer);
				buffer.clear();

				System.out.println("Inserted " + BATCH_SIZE + " records. Last inserted: " + customer);
			}
		}

		if (!buffer.isEmpty()) {
			customerRegion.putAll(buffer);
			buffer.clear();
		}
	}
}
