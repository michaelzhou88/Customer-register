import React from 'react';
import Container from './Container';
import { Avatar, Button } from 'antd';
import './Footer.css';

const Footer = (props) => (
    <div className='footer'>
        <Container>
            {props.numberOfCustomers !== undefined ? 
                <Avatar 
                    style={{backgroundColor: '#f56a00', marginRight: '5px'}} 
                    size='large'>{props.numberOfCustomers}</Avatar>: null
            }
            <Button onClick={() => props.handleAddCustomerClickEvent()} type='primary'>Add new customer +</Button>
        </Container>
    </div>
);

export default Footer;