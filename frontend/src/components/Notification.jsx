function Notification({ message, type, onClose }) {

    if (!message) {
        return null;
    }

    return (
        <div className={`notification ${type}`}>
            <span>{message}</span>

            <button onClick={onClose}>
                ×
            </button>
        </div>
    );
}

export default Notification;