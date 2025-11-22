import React from 'react';
import '../App.css'; // Використовуємо загальні стилі

const ConfirmModal = ({ message, onConfirm, onCancel }) => {
    return (
        <div className="modal-backdrop">
            <div className="modal-content-box">
                <p className="modal-message">{message}</p>
                <div className="modal-actions">
                    <button onClick={onCancel} className="modal-btn modal-btn-cancel">
                        Cancel
                    </button>
                    <button onClick={onConfirm} className="modal-btn modal-btn-confirm">
                        Confirm Delete
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmModal;