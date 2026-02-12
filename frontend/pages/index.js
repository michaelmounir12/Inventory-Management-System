import { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import {
  Box,
  Button,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
  Paper,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

const API_BASE_URL = 'http://localhost:8080/api/items';

export default function HomePage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [name, setName] = useState('');
  const [category, setCategory] = useState('');
  const [quantity, setQuantity] = useState('');
  const [location, setLocation] = useState('');

  const [searchName, setSearchName] = useState('');
  const [searchCategory, setSearchCategory] = useState('');

  const [editItem, setEditItem] = useState(null);
  const [editName, setEditName] = useState('');
  const [editCategory, setEditCategory] = useState('');
  const [editQuantity, setEditQuantity] = useState('');
  const [editLocation, setEditLocation] = useState('');

  const [saving, setSaving] = useState(false);

  const fetchItems = async () => {
    try {
      setLoading(true);
      setError('');
      const res = await axios.get(API_BASE_URL);
      setItems(res.data || []);
    } catch (e) {
      console.error(e);
      setError('Failed to load items.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchItems();
  }, []);

  const resetForm = () => {
    setName('');
    setCategory('');
    setQuantity('');
    setLocation('');
  };

  const handleAddItem = async (e) => {
    e.preventDefault();
    if (!name || !category || !quantity || !location) {
      setError('All fields are required to add an item.');
      return;
    }
    const qty = parseInt(quantity, 10);
    if (Number.isNaN(qty) || qty < 0) {
      setError('Quantity must be a non-negative number.');
      return;
    }

    try {
      setSaving(true);
      setError('');
      const res = await axios.post(API_BASE_URL, {
        name,
        category,
        quantity: qty,
        location,
      });
      // Update table in "real-time" by updating local state
      setItems((prev) => [...prev, res.data]);
      resetForm();
    } catch (e) {
      console.error(e);
      setError('Failed to add item.');
    } finally {
      setSaving(false);
    }
  };

  const openEditDialog = (item) => {
    setEditItem(item);
    setEditName(item.name);
    setEditCategory(item.category);
    setEditQuantity(String(item.quantity));
    setEditLocation(item.location);
  };

  const closeEditDialog = () => {
    setEditItem(null);
    setEditName('');
    setEditCategory('');
    setEditQuantity('');
    setEditLocation('');
  };

  const handleUpdateItem = async () => {
    if (!editItem) return;

    const qty = parseInt(editQuantity, 10);
    if (Number.isNaN(qty) || qty < 0) {
      setError('Quantity must be a non-negative number.');
      return;
    }

    try {
      setSaving(true);
      setError('');
      const res = await axios.put(`${API_BASE_URL}/${editItem.id}`, {
        name: editName,
        category: editCategory,
        quantity: qty,
        location: editLocation,
      });

      const updated = res.data;
      setItems((prev) =>
        prev.map((i) => (i.id === updated.id ? updated : i))
      );
      closeEditDialog();
    } catch (e) {
      console.error(e);
      setError('Failed to update item.');
    } finally {
      setSaving(false);
    }
  };

  const handleDeleteItem = async (id) => {
    if (!window.confirm('Are you sure you want to delete this item?')) {
      return;
    }
    try {
      setError('');
      await axios.delete(`${API_BASE_URL}/${id}`);
      setItems((prev) => prev.filter((i) => i.id !== id));
    } catch (e) {
      console.error(e);
      setError('Failed to delete item.');
    }
  };

  const filteredItems = useMemo(() => {
    return items.filter((item) => {
      const matchesName =
        !searchName ||
        item.name.toLowerCase().includes(searchName.toLowerCase());
      const matchesCategory =
        !searchCategory ||
        item.category.toLowerCase().includes(searchCategory.toLowerCase());
      return matchesName && matchesCategory;
    });
  }, [items, searchName, searchCategory]);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Inventory Management System
      </Typography>

      {error && (
        <Box mb={2}>
          <Typography color="error" variant="body2">
            {error}
          </Typography>
        </Box>
      )}

      <Box mb={4} component="form" onSubmit={handleAddItem}>
        <Typography variant="h6" gutterBottom>
          Add New Item
        </Typography>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} mb={2}>
          <TextField
            label="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            fullWidth
          />
          <TextField
            label="Category"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            fullWidth
          />
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} mb={2}>
          <TextField
            label="Quantity"
            type="number"
            value={quantity}
            onChange={(e) => setQuantity(e.target.value)}
            fullWidth
          />
          <TextField
            label="Location"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            fullWidth
          />
        </Stack>
        <Button type="submit" variant="contained" disabled={saving}>
          {saving ? 'Saving...' : 'Add Item'}
        </Button>
      </Box>

      <Box mb={2}>
        <Typography variant="h6" gutterBottom>
          Search / Filter
        </Typography>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Search by name"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            fullWidth
          />
          <TextField
            label="Filter by category"
            value={searchCategory}
            onChange={(e) => setSearchCategory(e.target.value)}
            fullWidth
          />
        </Stack>
      </Box>

      <Box mb={2} display="flex" justifyContent="space-between" alignItems="center">
        <Typography variant="h6">Items</Typography>
        <Button variant="outlined" onClick={fetchItems} disabled={loading}>
          {loading ? 'Refreshing...' : 'Refresh'}
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Category</TableCell>
              <TableCell>Quantity</TableCell>
              <TableCell>Location</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredItems.map((item) => (
              <TableRow key={item.id}>
                <TableCell>{item.id}</TableCell>
                <TableCell>{item.name}</TableCell>
                <TableCell>{item.category}</TableCell>
                <TableCell>{item.quantity}</TableCell>
                <TableCell>{item.location}</TableCell>
                <TableCell align="right">
                  <IconButton
                    aria-label="edit"
                    color="primary"
                    size="small"
                    onClick={() => openEditDialog(item)}
                    sx={{ mr: 1 }}
                  >
                    <EditIcon fontSize="small" />
                  </IconButton>
                  <IconButton
                    aria-label="delete"
                    color="error"
                    size="small"
                    onClick={() => handleDeleteItem(item.id)}
                  >
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {filteredItems.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  {loading ? 'Loading items...' : 'No items found.'}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Edit dialog */}
      <Dialog open={!!editItem} onClose={closeEditDialog} fullWidth maxWidth="sm">
        <DialogTitle>Edit Item</DialogTitle>
        <DialogContent>
          <Stack spacing={2} mt={1}>
            <TextField
              label="Name"
              value={editName}
              onChange={(e) => setEditName(e.target.value)}
              fullWidth
            />
            <TextField
              label="Category"
              value={editCategory}
              onChange={(e) => setEditCategory(e.target.value)}
              fullWidth
            />
            <TextField
              label="Quantity"
              type="number"
              value={editQuantity}
              onChange={(e) => setEditQuantity(e.target.value)}
              fullWidth
            />
            <TextField
              label="Location"
              value={editLocation}
              onChange={(e) => setEditLocation(e.target.value)}
              fullWidth
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={closeEditDialog}>Cancel</Button>
          <Button onClick={handleUpdateItem} variant="contained" disabled={saving}>
            {saving ? 'Saving...' : 'Save'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}

