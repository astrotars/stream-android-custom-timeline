import dotenv from 'dotenv';
import { StreamChat } from 'stream-chat';

dotenv.config();

exports.streamChatCredentials = async (req, res) => {
  try {
    const data = req.body;
    const apiKey = process.env.STREAM_API_KEY;
    const apiSecret = process.env.STREAM_API_SECRET;

    const client = new StreamChat(apiKey, apiSecret);

    const user = Object.assign({}, data, {
      id: req.user,
      role: 'user',
      image: `https://robohash.org/${req.user}`,
    });
    const token = client.createToken(user.id);
    await client.updateUser(user);

    res.status(200).json({ user, token, apiKey });
  } catch (error) {
    console.log(error);
    res.status(500).json({ error: error.message });
  }
};