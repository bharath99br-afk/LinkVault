function ConfirmModal({
    isOpen,
    title,
    message,
    confirmText = "Confirm",
    cancelText = "Cancel",
    onConfirm,
    onCancel,
}) {
    if (!isOpen) {
        return null;
    }

    return (
        <div
            className="modal-overlay"
            role="presentation"
            onMouseDown={onCancel}
        >
            <div
                className="confirm-modal"
                role="dialog"
                aria-modal="true"
                aria-labelledby="confirm-modal-title"
                aria-describedby="confirm-modal-message"
                onMouseDown={(event) => event.stopPropagation()}
            >
                <button
                    type="button"
                    className="modal-close-button"
                    onClick={onCancel}
                    aria-label="Close confirmation"
                >
                    ×
                </button>

                <div className="modal-icon" aria-hidden="true">
                    !
                </div>

                <div className="modal-content">
                    <h2 id="confirm-modal-title">
                        {title}
                    </h2>

                    <p id="confirm-modal-message">
                        {message}
                    </p>
                </div>

                <div className="modal-actions">
                    <button
                        type="button"
                        className="modal-cancel-button"
                        onClick={onCancel}
                    >
                        {cancelText}
                    </button>

                    <button
                        type="button"
                        className="modal-confirm-button"
                        onClick={onConfirm}
                    >
                        {confirmText}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ConfirmModal;