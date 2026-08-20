function LinkTable({ links }) {
    return (
        <div className="link-section">
            {links.length === 0 ? (
                <p>No links found.</p>
            ) : (
                <table className="link-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>URL</th>
                        </tr>
                    </thead>

                    <tbody>
                        {links.map((link) => (
                            <tr key={link.id}>
                                <td>{link.id}</td>
                                <td>{link.title}</td>
                                <td>
                                    <a
                                        href={link.url}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                    >
                                        {link.url}
                                    </a>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default LinkTable;