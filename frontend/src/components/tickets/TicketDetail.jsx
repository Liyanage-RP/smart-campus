import React, { useEffect, useState } from 'react';
import { ticketApi } from '../../api/ticketApi';

const TicketDetail = ({ ticketId, userId = 1, onBack }) => {
    const [ticket, setTicket] = useState(null);
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    const [editingCommentId, setEditingCommentId] = useState(null);
    const [editContent, setEditContent] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchTicketData();
    }, [ticketId]);

    const fetchTicketData = async () => {
        try {
            setLoading(true);
            const [ticketData, commentsData] = await Promise.all([
                ticketApi.getTicketById(ticketId),
                ticketApi.getComments(ticketId)
            ]);
            setTicket(ticketData);
            setComments(commentsData);
            setError('');
        } catch (err) {
            setError('Failed to load ticket details.');
        } finally {
            setLoading(false);
        }
    };

    const handleAddComment = async (e) => {
        e.preventDefault();
        if (!newComment.trim()) return;
        try {
            await ticketApi.addComment(ticketId, {
                authorId: userId,
                authorName: `User ${userId}`,
                content: newComment
            });
            setNewComment('');
            fetchTicketData();
        } catch (err) {
            setError('Failed to add comment.');
        }
    };

    const handleEditComment = async (commentId) => {
        try {
            await ticketApi.editComment(ticketId, commentId, userId, editContent);
            setEditingCommentId(null);
            setEditContent('');
            fetchTicketData();
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to edit comment.');
        }
    };

    const handleDeleteComment = async (commentId) => {
        if (!window.confirm('Are you sure you want to delete this comment?')) return;
        try {
            await ticketApi.deleteComment(ticketId, commentId, userId);
            fetchTicketData();
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to delete comment.');
        }
    };

    if (loading) return <div>Loading ticket...</div>;
    if (!ticket) return <div>Ticket not found.</div>;

    return (
        <div className="ticket-detail-container">
            <button onClick={onBack} className="btn btn-secondary">&larr; Back</button>

            <div className="ticket-detail-card">
                <h2>Ticket #{ticket.id} – {ticket.category}</h2>
                <div className="ticket-meta">
                    <span><strong>Location:</strong> {ticket.resourceLocation}</span>
                    <span><strong>Priority:</strong> <span className={`priority-badge priority-${ticket.priority.toLowerCase()}`}>{ticket.priority}</span></span>
                    <span><strong>Status:</strong> <span className={`status-badge status-${ticket.status.toLowerCase()}`}>{ticket.status}</span></span>
                    {ticket.assignedTechnicianId && <span><strong>Assigned Technician:</strong> {ticket.assignedTechnicianId}</span>}
                    <span><strong>Contact:</strong> {ticket.contactDetails || 'N/A'}</span>
                    <span><strong>Created:</strong> {new Date(ticket.createdAt).toLocaleString()}</span>
                </div>

                <div className="ticket-description">
                    <h4>Description</h4>
                    <p>{ticket.description}</p>
                </div>

                {ticket.resolutionNotes && (
                    <div className="ticket-resolution">
                        <h4>Resolution Notes</h4>
                        <p>{ticket.resolutionNotes}</p>
                    </div>
                )}

                {ticket.adminReason && (
                    <div className="ticket-rejection">
                        <h4>Rejection Reason</h4>
                        <p>{ticket.adminReason}</p>
                    </div>
                )}

                {ticket.attachments && ticket.attachments.length > 0 && (
                    <div className="ticket-attachments">
                        <h4>Attachments ({ticket.attachments.length}/3)</h4>
                        <div className="attachment-list">
                            {ticket.attachments.map(att => (
                                <div key={att.id} className="attachment-item">
                                    <span>📎 {att.fileName}</span>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>

            {/* Comments Section */}
            <div className="ticket-comments">
                <h3>Comments ({comments.length})</h3>
                {error && <div className="alert alert-error">{error}</div>}

                {comments.map(comment => (
                    <div key={comment.id} className="comment-card">
                        <div className="comment-header">
                            <strong>{comment.authorName}</strong>
                            <span className="comment-time">{new Date(comment.createdAt).toLocaleString()}</span>
                        </div>

                        {editingCommentId === comment.id ? (
                            <div className="comment-edit">
                                <textarea
                                    value={editContent}
                                    onChange={e => setEditContent(e.target.value)}
                                    rows="3"
                                />
                                <button onClick={() => handleEditComment(comment.id)} className="btn btn-primary btn-sm">Save</button>
                                <button onClick={() => setEditingCommentId(null)} className="btn btn-secondary btn-sm">Cancel</button>
                            </div>
                        ) : (
                            <>
                                <p className="comment-content">{comment.content}</p>
                                {comment.authorId === userId && (
                                    <div className="comment-actions">
                                        <button onClick={() => { setEditingCommentId(comment.id); setEditContent(comment.content); }} className="btn btn-warning btn-sm">Edit</button>
                                        <button onClick={() => handleDeleteComment(comment.id)} className="btn btn-danger btn-sm">Delete</button>
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                ))}

                {/* Add Comment Form */}
                <form onSubmit={handleAddComment} className="comment-form">
                    <textarea
                        value={newComment}
                        onChange={e => setNewComment(e.target.value)}
                        placeholder="Write a comment..."
                        rows="3"
                        required
                    />
                    <button type="submit" className="btn btn-primary">Post Comment</button>
                </form>
            </div>
        </div>
    );
};

export default TicketDetail;
