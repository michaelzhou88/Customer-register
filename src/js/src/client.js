import fetch from 'unfetch';

const checkStatus = response => {
    if (response.ok) {
        return response;
    } else {
        let error = new Error(response.statusText);
        error.response = response;
        response.json().then(e => {
            error.error = e; 
        });
        return Promise.reject(error);
    }
}

export const getAllCustomers = () => fetch('/api/customers').then(checkStatus);

export const addNewCustomer = customer => 
    fetch('api/customers', {
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(customer)
    }).then(checkStatus);

export const updateCustomer = (customerId, customer) => 
    fetch(`api/customers/${customerId}`, {
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'PUT',
        body: JSON.stringify(customer)
    }).then(checkStatus);

export const deleteCustomer = customerId => 
    fetch(`api/customers/${customerId}`, {
        method: 'DELETE'
    })
    .then(checkStatus);
