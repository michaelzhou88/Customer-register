import React, { Component, Fragment } from 'react'; 
import Container from './Container';
import Footer from './Footer';
import './App.css';
import { getAllCustomers, updateCustomer, deleteCustomer } from './client';
import AddCustomerForm from './forms/AddCustomerForm';
import EditCustomerForm from './forms/EditCustomerForm';
import { successNotification, errorNotification } from './Notification';
import {
  Table,
  Avatar,
  Spin,
  Modal, 
  Empty,
  PageHeader,
  Button, 
  notification,
  Popconfirm
} from 'antd';
import Icon from '@ant-design/icons';

const getIndicatorIcon = () => <Icon type="loading" style={{ fontSize: 24 }} spin/>

class App extends Component {
  state = {
    customers : [],
    isFetching: false,
    selectedCustomer: {},
    isAddCustomerModalVisible: false,
    isEditCustomerModalVisible: false
  }

  componentDidMount () {
    this.fetchCustomers();
  }

  openAddCustomerModal = () => this.setState({isAddCustomerModalVisible: true});

  closeAddCustomerModal = () => this.setState({isAddCustomerModalVisible: false});

  openEditCustomerModal = () => this.setState({isEditCustomerModalVisible: true});

  closeEditCustomerModal = () => this.setState({isEditCustomerModalVisible: false});

  openNotificationWithIcon = (type, message, description) => notification[type][{message, description}];

  fetchCustomers = () => {
    this.setState({
      isFetching: true
    });
    getAllCustomers()
    .then(res => res.json()
    .then(customers => {
      console.log(customers);
      this.setState({
        customers,
        isFetching: false
      });
    }))
    .catch(error => {
      console.log(error.error);
      const message = error.error.message;
      const description = error.error.error;
      errorNotification(message, description);
      this.setState({
        isFetching: false
      });
    });
  }

  editUser = selectedCustomer => {
    this.setState({ selectedCustomer });
    this.openEditCustomerModal();
  }

  updateCustomerFormSubmitter = customer => {
    updateCustomer(customer.customerId, customer).then(() => {
      successNotification('Success', `${customer.customerId} has been updated`);
      this.openNotificationWithIcon('success', 'Customer updated', `${customer.customerId} has been updated`);
      this.closeEditCustomerModal();
      this.fetchCustomers();
    }).catch(err => {
      console.log(err.error);
      this.openNotificationWithIcon(
        'error',
        'error',
        `(${err.error.status}) ${err.error.error}`
      );
      errorNotification('error', `(${err.error.status}) ${err.error.error}`);
    });
  }

  deleteCustomer = customerId => {
    deleteCustomer(customerId).then(() => {
      successNotification('Success', `${customerId} was deleted`);
      this.fetchCustomers();
    }).catch(err => {
      errorNotification('error', 'error', `(${err.error.status}) ${err.error.error}`);
    });
  }

  render() {

    const { customers, isFetching, isAddCustomerModalVisible } = this.state;

    const commonElements = () => (
      <div>
        <Modal
          title='Add new customer'
          visible={isAddCustomerModalVisible}
          onOk={this.closeAddCustomerModal}
          onCancel={this.closeAddCustomerModal}
          width={1000}>
          <AddCustomerForm 
            onSuccess={() => {
              this.closeAddCustomerModal();
              this.fetchCustomers();
            }}
            onFailure={(error) => {
              const message = error.error.message;
              const description = error.error.httpStatus;
              errorNotification(message, description);
            }}
          />
        </Modal>

        <Modal 
          title='Edit'
          visible={this.state.isEditCustomerModalVisible}
          onOk={this.closeEditCustomerModal}
          onCancel={this.closeEditCustomerModal}
          width={1000}>

        <PageHeader title={`${this.state.selectedCustomer.customerId}`}/>

        <EditCustomerForm 
            initialValues={this.state.selectedCustomer} 
            submitter={this.updateCustomerFormSubmitter}/>
        </Modal>

        <Footer 
          numberOfCustomers={customers.length}
          handleAddCustomerClickEvent={this.openAddCustomerModal}/>
      </div>
    )

    if (isFetching) {
      return (
        <Container>
          <Spin indicator={getIndicatorIcon()}/>
        </Container>
      );
    }

    if (customers && customers.length) { 
      const columns = [
        {
          title: '',
          key: 'avatar',
          render: (text, customer) => (
            <Avatar size='large'>
              {`${customer.firstName.charAt(0).toUpperCase()}${customer.lastName.charAt(0).toUpperCase()}`}
            </Avatar>
          )
        },
        {
          title: 'Customer Id',
          dataIndex: 'customerId',
          key: 'customerId'
        },
        {
          title: 'First Name',
          dataIndex: 'firstName',
          key: 'firstName'
        },
        {
          title: 'Last Name',
          dataIndex: 'lastName',
          key: 'lastName'
        },
        {
          title: 'Email',
          dataIndex: 'email',
          key: 'email'
        },
        {
          title: 'Gender',
          dataIndex: 'gender',
          key: 'gender'
        },
        {
          title: 'Action',
          key: 'action',
          render: (record) => (
            <Fragment>
              <Popconfirm
                placement='topRight'
                title={`Are you sure you want to delete ${record.customerId}`}
                onConfirm={() => this.deleteCustomer(record.customerId)} okText='Yes' cancelText='No'
                onCancel={e => e.stopPropagation()}>
                <Button type='danger' onClick={(e) => e.stopPropagation()}>Delete</Button>
              </Popconfirm>
              <Button
                style={{marginLeft: '5px'}} type='primary' onClick={() => this.editUser(record)}>Edit</Button>
            </Fragment>
          ),
        }
      ];

      return ( 
        <Container>
          <Table 
            style={{marginBottom: '100px'}}
            dataSource={customers} 
            columns={columns} 
            pagination={false}
            rowKey='customerId'/>
            {commonElements()}
        </Container>
        );
    }

    return ( 
      <Container>
        <Empty description={
          <h1> No Customers Found </h1>
        } />
        {commonElements()}
      </Container>
    )
  }
}

export default App;
