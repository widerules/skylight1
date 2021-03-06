package skylight1.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.util.FloatMath;
import android.util.Log;

/**
 * Encapsulates the construction of OpenGLGeometry objects.
 */
class OpenGLGeometryBuilderImpl<T, R> extends GeometryBuilderImpl<T, R> implements OpenGLGeometryBuilder<T, R> {
	// TODO decide what to do about clients not bothering to provide textures (for example) for some of the components
	// of a geometry. this will not work as is! Simple solution would be to add zeros in the add methods, and use set in
	// the inner classes

	/**
	 * Inner class that permits the addition of texture, normalComponents, and colours to a 3D triangle.
	 */
	public class Triangle3D<X> implements TexturableTriangle3D<X>, ColourableTriangle3D<X>, NormalizableTriangle3D<X> {
		public X setTextureCoordinates(float aU1, float aV1, float aU2, float aV2, float aU3, float aV3) {
			final int offset = vertexOffsetOfCurrentGeometry * TEXTURE_COORDINATES_PER_VERTEX;

			textureCoordinatesAsBuffer.put(offset, (int) (aU1 * (1 << 16)));
			textureCoordinatesAsBuffer.put(offset + 1, (int) (aV1 * (1 << 16)));

			textureCoordinatesAsBuffer.put(offset + 2, (int) (aU2 * (1 << 16)));
			textureCoordinatesAsBuffer.put(offset + 3, (int) (aV2 * (1 << 16)));

			textureCoordinatesAsBuffer.put(offset + 4, (int) (aU3 * (1 << 16)));
			textureCoordinatesAsBuffer.put(offset + 5, (int) (aV3 * (1 << 16)));

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setNormal(float aNormalX1, float aNormalY1, float aNormalZ1, float aNormalX2, float aNormalY2,
				float aNormalZ2, float aNormalX3, float aNormalY3, float aNormalZ3) {
			final int offset = vertexOffsetOfCurrentGeometry * NORMAL_COMPONENTS_PER_VERTEX;

			normalsAsBuffer.put(offset, (int) (aNormalX1 * (1 << 16)));
			normalsAsBuffer.put(offset + 1, (int) (aNormalY1 * (1 << 16)));
			normalsAsBuffer.put(offset + 2, (int) (aNormalZ1 * (1 << 16)));

			normalsAsBuffer.put(offset + 3, (int) (aNormalX2 * (1 << 16)));
			normalsAsBuffer.put(offset + 4, (int) (aNormalY2 * (1 << 16)));
			normalsAsBuffer.put(offset + 5, (int) (aNormalZ2 * (1 << 16)));

			normalsAsBuffer.put(offset + 6, (int) (aNormalX3 * (1 << 16)));
			normalsAsBuffer.put(offset + 7, (int) (aNormalY3 * (1 << 16)));
			normalsAsBuffer.put(offset + 8, (int) (aNormalZ3 * (1 << 16)));

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setColour(float aRed1, float aGreen1, float aBlue1, float aRed2, float aGreen2, float aBlue2,
				float aRed3, float aGreen3, float aBlue3) {
			final int offset = vertexOffsetOfCurrentGeometry * COLOUR_PARTS_PER_VERTEX;

			coloursAsBuffer.put(offset, (int) (aRed1 * (1 << 16)));
			coloursAsBuffer.put(offset + 1, (int) (aGreen1 * (1 << 16)));
			coloursAsBuffer.put(offset + 2, (int) (aBlue1 * (1 << 16)));

			coloursAsBuffer.put(offset + 3, (int) (aRed2 * (1 << 16)));
			coloursAsBuffer.put(offset + 4, (int) (aGreen2 * (1 << 16)));
			coloursAsBuffer.put(offset + 5, (int) (aBlue2 * (1 << 16)));

			coloursAsBuffer.put(offset + 6, (int) (aRed3 * (1 << 16)));
			coloursAsBuffer.put(offset + 7, (int) (aGreen3 * (1 << 16)));
			coloursAsBuffer.put(offset + 8, (int) (aBlue3 * (1 << 16)));

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}
	}

	/**
	 * Inner class that permits the addition of texture and colours to a 2D rectangle.
	 */
	public class Rectangle2D<X> implements TexturableRectangle2D<X>, ColourableRectangle2D<X> {
		public X setTextureCoordinates(float aU, float aV, float aUWidth, float aVHeight) {
			final int offset = vertexOffsetOfCurrentGeometry * TEXTURE_COORDINATES_PER_VERTEX;

			final int u1 = (int) (aU * (1 << 16));
			final int v1 = (int) (aV * (1 << 16));
			final int u2 = (int) ((aU + aUWidth) * (1 << 16));
			final int v2 = (int) ((aV + aVHeight) * (1 << 16));

			// first triangle of rectangle
			textureCoordinatesAsBuffer.put(offset, u1);
			textureCoordinatesAsBuffer.put(offset + 1, v1);

			textureCoordinatesAsBuffer.put(offset + 2, u2);
			textureCoordinatesAsBuffer.put(offset + 3, v1);

			textureCoordinatesAsBuffer.put(offset + 4, u2);
			textureCoordinatesAsBuffer.put(offset + 5, v2);

			// second triangle of rectangle
			textureCoordinatesAsBuffer.put(offset + 6, u2);
			textureCoordinatesAsBuffer.put(offset + 7, v2);

			textureCoordinatesAsBuffer.put(offset + 8, u1);
			textureCoordinatesAsBuffer.put(offset + 9, v2);

			textureCoordinatesAsBuffer.put(offset + 10, u1);
			textureCoordinatesAsBuffer.put(offset + 11, v1);

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}

		public X setColour(float aRed1, float aGreen1, float aBlue1, float aRed2, float aGreen2, float aBlue2,
				float aRed3, float aGreen3, float aBlue3, float aRed4, float aGreen4, float aBlue4) {
			final int offset = vertexOffsetOfCurrentGeometry * COLOUR_PARTS_PER_VERTEX;

			// first triangle of rectangle
			coloursAsBuffer.put(offset, (int) (aRed1 * (1 << 16)));
			coloursAsBuffer.put(offset + 1, (int) (aGreen1 * (1 << 16)));
			coloursAsBuffer.put(offset + 2, (int) (aBlue1 * (1 << 16)));

			coloursAsBuffer.put(offset + 3, (int) (aRed2 * (1 << 16)));
			coloursAsBuffer.put(offset + 4, (int) (aGreen2 * (1 << 16)));
			coloursAsBuffer.put(offset + 5, (int) (aBlue2 * (1 << 16)));

			coloursAsBuffer.put(offset + 6, (int) (aRed3 * (1 << 16)));
			coloursAsBuffer.put(offset + 7, (int) (aGreen3 * (1 << 16)));
			coloursAsBuffer.put(offset + 8, (int) (aBlue3 * (1 << 16)));

			// second triangle of rectangle
			coloursAsBuffer.put(offset + 9, (int) (aRed3 * (1 << 16)));
			coloursAsBuffer.put(offset + 10, (int) (aGreen3 * (1 << 16)));
			coloursAsBuffer.put(offset + 11, (int) (aBlue3 * (1 << 16)));

			coloursAsBuffer.put(offset + 12, (int) (aRed2 * (1 << 16)));
			coloursAsBuffer.put(offset + 13, (int) (aGreen2 * (1 << 16)));
			coloursAsBuffer.put(offset + 14, (int) (aBlue2 * (1 << 16)));

			coloursAsBuffer.put(offset + 15, (int) (aRed4 * (1 << 16)));
			coloursAsBuffer.put(offset + 16, (int) (aGreen4 * (1 << 16)));
			coloursAsBuffer.put(offset + 17, (int) (aBlue4 * (1 << 16)));

			@SuppressWarnings("unchecked")
			X typeSafeThis = (X) this;

			return typeSafeThis;
		}
	}

	static final int MODEL_COORDINATES_PER_VERTEX = 3;

	static final int TEXTURE_COORDINATES_PER_VERTEX = 2;

	static final int NORMAL_COMPONENTS_PER_VERTEX = 3;

	static final int COLOUR_PARTS_PER_VERTEX = 3;

	private static final int VERTICES_PER_TRIANGLE = 3;

	private static final int TRIANGLES_PER_RECTANGLE = 2;

	private static final int NO_MODE = -1;

	private static int currentMode = NO_MODE;

	private Texture currentTexture;

	private Stack<Integer> geometryStartVertexStack;

	private T triangle3D;

	private R rectangle2D;

	int vertexOffsetOfCurrentGeometry;

	int vertexOffsetOfNextGeometry;

	IntBuffer modelCoordinatesAsBuffer;

	IntBuffer textureCoordinatesAsBuffer;

	IntBuffer normalsAsBuffer;

	IntBuffer coloursAsBuffer;

	boolean complete;
	
	{
		init();
	}

	/**
	 * @param aUsesTexturesCoordinates
	 *            indicates if this geometries will include textures.
	 * @param aUsesNormals
	 *            indicates if this geometries will include normalComponents.
	 * @param aUsesColours
	 *            indicates if this geometries will include colours.
	 */
	public OpenGLGeometryBuilderImpl(final boolean aUsesTexturesCoordinates, final boolean aUsesNormals,
			final boolean aUsesColours, int aNumberOfVertices) {
		super(aUsesTexturesCoordinates, aUsesNormals, aUsesColours);
				
		modelCoordinatesAsBuffer = createDirectIntBuffer(aNumberOfVertices, MODEL_COORDINATES_PER_VERTEX);

		if (aUsesTexturesCoordinates) {
			textureCoordinatesAsBuffer = createDirectIntBuffer(aNumberOfVertices, TEXTURE_COORDINATES_PER_VERTEX );
		}

		if (aUsesNormals) {
			normalsAsBuffer = createDirectIntBuffer(aNumberOfVertices, NORMAL_COMPONENTS_PER_VERTEX);
		}

		if (aUsesColours) {
			coloursAsBuffer = createDirectIntBuffer(aNumberOfVertices, COLOUR_PARTS_PER_VERTEX);
		}
	}

	private IntBuffer createDirectIntBuffer(final int aNumberOfVertices, final int modelCoordinatesPerVertex) {
		// create a direct byte buffer in native byte order
		final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(modelCoordinatesPerVertex * aNumberOfVertices * Integer.SIZE / Byte.SIZE);
		byteBuffer.order(ByteOrder.nativeOrder());
		final IntBuffer byteBufferAsIntBuffer = byteBuffer.asIntBuffer();
		
		// point the buffer back to the beginning
		byteBufferAsIntBuffer.position(0);

		return byteBufferAsIntBuffer;
	}

	/**
	 * Adds a 3D triangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Triangle3D, which permits adding textures, colour and normalComponents as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	@Override
	public T add3DTriangle(float anX1, float aY1, float aZ1, float anX2, float aY2, float aZ2, float anX3, float aY3,
			float aZ3) {
		vertexOffsetOfCurrentGeometry = vertexOffsetOfNextGeometry;
		vertexOffsetOfNextGeometry += VERTICES_PER_TRIANGLE;

		checkCurrentGeometryAndCompleteAndCheckAndSetMode(GL10.GL_TRIANGLES);

		updateArraySizesIfNecessary();

		final int offset = vertexOffsetOfCurrentGeometry * MODEL_COORDINATES_PER_VERTEX;

		modelCoordinatesAsBuffer.put(offset, (int) (anX1 * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset+ 1, (int) (aY1 * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset+ 2, (int) (aZ1 * (1 << 16)));

		modelCoordinatesAsBuffer.put(offset + 3, (int) (anX2 * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 4, (int) (aY2 * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 5, (int) (aZ2 * (1 << 16)));

		modelCoordinatesAsBuffer.put(offset + 6, (int) (anX3 * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 7, (int) (aY3 * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 8, (int) (aZ3 * (1 << 16)));

		return triangle3D;
	}

	/**
	 * Adds a 2D (z = 0) upright rectangle to the current geometry. Uses mode GL_TRIANGLES.
	 * 
	 * @return A Rectangle2D, which permits adding textures and colour as per the configuration of the
	 *         OpenGLGeometryBuilder
	 */
	@Override
	public R add2DRectangle(float anX, float aY, float aWidth, float aHeight) {
		vertexOffsetOfCurrentGeometry = vertexOffsetOfNextGeometry;
		vertexOffsetOfNextGeometry += VERTICES_PER_TRIANGLE * TRIANGLES_PER_RECTANGLE;

		checkCurrentGeometryAndCompleteAndCheckAndSetMode(GL10.GL_TRIANGLES);

		updateArraySizesIfNecessary();

		final int offset = vertexOffsetOfCurrentGeometry * MODEL_COORDINATES_PER_VERTEX;

		// triangle 1
		modelCoordinatesAsBuffer.put(offset, (int) (anX * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 1, (int) (aY * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 2, 0);

		modelCoordinatesAsBuffer.put(offset + 3, (int) ((anX + aWidth) * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 4, (int) (aY * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 5, 0);

		modelCoordinatesAsBuffer.put(offset + 6, (int) ((anX + aWidth) * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 7, (int) ((aY + aHeight) * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 8, 0);

		// triangle 2
		modelCoordinatesAsBuffer.put(offset + 9, (int) ((anX + aWidth) * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 10, (int) ((aY + aHeight) * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 11, 0);

		modelCoordinatesAsBuffer.put(offset + 12, (int) (anX * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 13, (int) ((aY + aHeight) * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 14, 0);

		modelCoordinatesAsBuffer.put(offset + 15, (int) (anX * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 16, (int) (aY * (1 << 16)));
		modelCoordinatesAsBuffer.put(offset + 17, 0);

		return rectangle2D;
	}

	private void updateArraySizesIfNecessary() {
		if (modelCoordinatesAsBuffer.capacity() < vertexOffsetOfNextGeometry * MODEL_COORDINATES_PER_VERTEX) {
			throw new IllegalArgumentException("Attempting to add more vertices than were provided to the constructor");
		}
	}

	/**
	 * Used to mark the start of a new geometry. The end of the geometry is marked by a matching call to endGeometry. A
	 * given geometry must contain all triangles, triangle strips, triangle fans, lines, line strips, or points, not a
	 * mix there-of. All triangles, triangle strips, triangle fans, lines, line strips, or points added between the
	 * matching pair of startGeometry/endGeometry will belong to the single OpenGLGeometry object returned by
	 * endGeometry. Geometries may be nested, so two calls to startGeometry may be followed by two calls to endGeometry.
	 */
	public void startGeometry(final Texture aTexture) {
		if (complete) {
			throw new IllegalStateException("Cannot start geometry after complete");
		}

		// nested geometries must use the same texture as their parent
		if (!geometryStartVertexStack.isEmpty()) {
			if (!currentTexture.equals(aTexture)) {
				throw new IllegalStateException(String.format("Cannnot start a nested geometry with a different texture than its parent: %s, %s", currentTexture, aTexture));
			}
		} else {
			if (geometryStartVertexStack.isEmpty()) {
				currentTexture = aTexture;
			}
		}

		int firstVertexOffset = vertexOffsetOfNextGeometry;
		geometryStartVertexStack.push(firstVertexOffset);
	}

	/**
	 * @see OpenGLGeometryBuilderImpl#startGeometry()
	 * @return The returned OpenGLGeometry must not be used until <i>after</i> this OpenGLGeometryBuilder has been
	 *         enabled.
	 */
	public OpenGLGeometry endGeometry() {
		if (geometryStartVertexStack.isEmpty()) {
			throw new IllegalStateException("calls to endGeometry must match calls to startGeometry");
		}

		int firstVertexOffset = geometryStartVertexStack.pop();
		final int numberOfVertices = vertexOffsetOfNextGeometry - firstVertexOffset;
		final float[] sphere = calculateBoundingSphere(modelCoordinatesAsBuffer, firstVertexOffset, numberOfVertices);
		final OpenGLGeometry openGLGeometry = new OpenGLGeometry(currentMode, firstVertexOffset, numberOfVertices, this, sphere, currentTexture);

		// if the stack is empty, then clear the current mode: the next geometry can use a different mode
		if (geometryStartVertexStack.isEmpty()) {
			currentMode = NO_MODE;
			currentTexture = null;
		}

		return openGLGeometry;
	}

	private float[] calculateBoundingSphere(final IntBuffer aModelCoordinates, final int aFirstVertexOffset, final int aNumberOfVertices) {
		int coordinateIndex = aFirstVertexOffset * MODEL_COORDINATES_PER_VERTEX;
		float minX = aModelCoordinates.get(coordinateIndex++);
		float minY = aModelCoordinates.get(coordinateIndex++);
		float minZ = aModelCoordinates.get(coordinateIndex++);
		float maxX = minX;
		float maxY = minY;
		float maxZ = minZ;
		for (int vertexIndex = 1; vertexIndex < aNumberOfVertices; vertexIndex++) {
			int x = aModelCoordinates.get(coordinateIndex++);
			int y = aModelCoordinates.get(coordinateIndex++);
			int z = aModelCoordinates.get(coordinateIndex++);
			minX = Math.min(minX, x);
			minY = Math.min(minY, y);
			minZ = Math.min(minZ, z);
			maxX = Math.max(maxX, x);
			maxY = Math.max(maxY, y);
			maxZ = Math.max(maxZ, z);
		}
		minX = minX / (1 << 16);
		maxX = maxX / (1 << 16);
		minY = minY / (1 << 16);
		maxY = maxY / (1 << 16);
		minZ = minZ / (1 << 16);
		maxZ = maxZ / (1 << 16);
		float dX = maxX - minX;
		float dY = maxY - minY;
		float dZ = maxZ - minZ;
		return new float[] { (minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2,
				FloatMath.sqrt(dX * dX + dY * dY + dZ * dZ) / 2 };
	}

	/**
	 * Completes the use of this builder for building. Must be called before using any of the OpenGLGeometry created by
	 * this object.
	 */
	private void complete() {
		// TODO allow the client to choose interleaved arrays or not. interleaved arrays are better for updating the
		// model coordinates, normals, colour and textures coordinates all at once, but non-interleaved are better for
		// changing only one aspect of a geometry at a time, such as during texture animations on fixed models
		// coordinates
		if (!geometryStartVertexStack.isEmpty()) {
			throw new IllegalStateException("Cannot complete until all started geometries are ended");
		}

		Log.i(OpenGLGeometryBuilderImpl.class.getName(), "number of vertices is " + vertexOffsetOfNextGeometry
				+ ", create with this number of vertices to avoid extending arrays");

		complete = true;
	}

	/**
	 * Enables all of the features necessary to render any of the geometries created by this builder.
	 */
	public void enable(GL10 aGL10) {
		if (!complete) {
			complete();
		}
		aGL10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		aGL10.glVertexPointer(3, GL10.GL_FIXED, 0, modelCoordinatesAsBuffer);
		if (usesTextureCoordinates) {
			aGL10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			aGL10.glTexCoordPointer(2, GL10.GL_FIXED, 0, textureCoordinatesAsBuffer);
		} else {
			aGL10.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		if (usesNormals) {
			aGL10.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			aGL10.glNormalPointer(GL10.GL_FIXED, 0, normalsAsBuffer);
		} else {
			aGL10.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
		if (usesColours) {
			aGL10.glEnableClientState(GL10.GL_COLOR_ARRAY);
			aGL10.glNormalPointer(GL10.GL_FIXED, 0, coloursAsBuffer);
		} else {
			aGL10.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}

	private void checkCurrentGeometryAndCompleteAndCheckAndSetMode(int aMode) {
		if (complete) {
			throw new IllegalStateException("Cannot add geometry to a completed builder");
		}

		if (geometryStartVertexStack.isEmpty()) {
			throw new IllegalStateException("Adding points, lines, and shapes may only be made between matched calls to startGeometry and endGeometry");
		}

		// if no mode has been set yet (i.e., this is the first triangle, line, etc. for this geometry), then use the
		// provided mode
		if (currentMode == NO_MODE) {
			currentMode = aMode;
		} else if (currentMode != aMode) {
			// TODO show textual values for modes
			throw new IllegalStateException(String.format("Cannot change the mode (point, line, triangle, triangle strip, or triangle fan) within a geometry; current is %d, new is %d", currentMode, aMode));
		}
	}

	@Override
	public boolean isBuildingGeometry() {
		return ! geometryStartVertexStack.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	private void init() {

		currentTexture = null;

		geometryStartVertexStack = new Stack<Integer>();

		triangle3D = (T) new Triangle3D<Object>();

		rectangle2D = (R) new Rectangle2D<Object>();

		vertexOffsetOfCurrentGeometry = 0;

		vertexOffsetOfNextGeometry = 0;

		complete = false;
	}

	@Override
	public void reset() {
		currentMode = NO_MODE;
		init();
	}
}
