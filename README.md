# Face_Lock

This is a face lock application which uses in-built face detection feature and one of the face recognition algorithm.

It is coded to work in landscape mode. Even though it serves the purpose I couldn't disable the "Home Button" (If it was simple anyone could create an application with which they can lock you out of your phone).

Algorithm -------->
  1. Open the front camera.
  2. Use in-built face detection feature to detect a face.
  3. Take a picture (if face score is high) automatically.
  4. Crop the face region from the picture.
  5. Convert RGB picture into grayscale image.
  6. Use difference of gaussioan filter method to eliminate noise and retrieve face features.
  7. Use histogram normalisation.
  8. Compare neigbouring pixels with center pixel.(Converting them to 1's and 0's)
  9. Convert the binary pixals to decimal and store these values. 
